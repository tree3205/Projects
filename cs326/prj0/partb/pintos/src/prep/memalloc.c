#include "memalloc.h"
#include <pthread.h>

bool my_list_less_func(const struct list_elem *a,
                       const struct list_elem *b, void *aux)
{
  struct free_block *f1, *f2;

  f1 = list_entry(a, struct free_block, elem);
  f2 = list_entry(b, struct free_block, elem);

  return f1 < f2;
}


/* Returns true if ELEM is a head, false otherwise. */
static inline bool
is_head (struct list_elem *elem)
{
  return elem != NULL && elem->prev == NULL && elem->next != NULL;
}

/* Returns true if ELEM is an interior element,
   false otherwise. */
static inline bool
is_interior (struct list_elem *elem)
{
  return elem != NULL && elem->prev != NULL && elem->next != NULL;
}

/* Returns true if ELEM is a tail, false otherwise. */
static inline bool
is_tail (struct list_elem *elem)
{
  return elem != NULL && elem->prev != NULL && elem->next == NULL;
}


/* Create a free_list to track the free_block*/
static struct list free_list;

/* global variables shared among threads */
pthread_mutex_t gmutex = PTHREAD_MUTEX_INITIALIZER;

/* Initialize memory allocator to use 'length' bytes of memory at 'base'.
   Build a free_block header in memory and initilize the free_list*/
void mem_init(uint8_t *base, size_t length)
{
    pthread_mutex_lock(&gmutex);

    struct free_block *fb = (struct free_block *)base;
    list_init(&free_list);
    fb->length = length;
    list_push_front(&free_list, &fb->elem);

    pthread_mutex_unlock(&gmutex);
}

/* Allocate 'length' bytes of memory. */
void * mem_alloc(size_t length)
{
    pthread_mutex_lock(&gmutex);

    if (list_size(&free_list) == 0)
    {
        pthread_mutex_unlock(&gmutex);
        return NULL;
    }

    /* Situation 1: request memory is too small.
    *  Check if allocated blocks is at least as
    * large as a free block header - otherwise, if a block is freed,
    * it would be impossible to reinsert that block into thefree list
    */
    if (length + sizeof(struct used_block) < sizeof(struct free_block))
    {
        length = sizeof(struct free_block) - sizeof(struct used_block);
    }

    /* There will be another two situations */
    struct list_elem *e;
    for (e = list_begin(&free_list); e != list_end(&free_list);
         e = list_next(e))
    {
        /* Find the address of the free_block*/
        struct free_block *fb = list_entry (e, struct free_block, elem);
        size_t *fb_length = &fb->length;

        /* Situaion 2: request memory is suitible, just slplit free_block*/
        if ((*fb_length - sizeof(struct free_block)) >=
            (sizeof(struct used_block) + length))
        {
            size_t ub_offset = *fb_length - length - sizeof(struct used_block);
            struct used_block *ub = (struct used_block *)((unsigned long)fb + ub_offset);
            ub->length = length + sizeof(struct used_block);
            *fb_length = *fb_length - ub->length;
            pthread_mutex_unlock(&gmutex);
            return &ub->data;
        }

        /* Situation 3: request memory is big, has to remove free header */
        if (sizeof(struct used_block) + length <= *fb_length)
        {
            struct used_block *ub = (struct used_block *)fb;
            length = *fb_length - sizeof(struct used_block);
            ub->length = *fb_length;
            list_remove(e);
            pthread_mutex_unlock(&gmutex);
            return &ub->data;
        }
    }
    pthread_mutex_unlock(&gmutex);
    return NULL;
}

/* Free memory pointed to by 'ptr'.
   If memory is freed, you must find the beginning of the block of memory
   that contains the address of the pointer passed to the free routine.
   That block of memory must be added to the free list.
   In addition, you'll have to coalesce the free list:
   if the blocks to the left and/or right of the block being freed are also free,
   they must be merged into a single block.
*/

void mem_free(void *ptr)
{
    /* Find the address of used_block
     * and create a free_block in the same place.
     * Add the new free_block into free_list.
     */

    pthread_mutex_lock(&gmutex);
    struct used_block *ub = (struct used_block *)((unsigned long) ptr
                            - sizeof(struct used_block));
    size_t *ub_length = &ub->length;
    struct free_block *new_fb = (struct free_block *)ub;
    new_fb->length = *ub_length;
    list_insert_ordered (&free_list, &new_fb->elem, my_list_less_func, NULL);

    /* Check the neighbours of the new created free_block in free_list*/
    struct list_elem *new_elem = &new_fb->elem;
    size_t *new_fb_length = &new_fb->length;
    struct list_elem *prev_elem = new_fb->elem.prev;
    struct list_elem *next_elem = new_fb->elem.next;

    /* case1: left is head, right is not tail:current is the first elem. */
    if (is_head(prev_elem) && !is_tail(next_elem))
    {
        struct free_block *next_fb = list_entry (next_elem, struct free_block, elem);
        size_t *next_fb_length = &next_fb->length;
        if ((unsigned long)new_fb + *new_fb_length == (unsigned long)next_fb)
        {
            *new_fb_length = *new_fb_length + *next_fb_length;
            list_remove(&next_fb->elem);
        }
    }

    /*case2: left is not head, right is tail:current is the last elem */
    if (!is_head(prev_elem) && is_tail(next_elem))
    {
        struct free_block *prev_fb = list_entry (prev_elem, struct free_block, elem);
        size_t *prev_fb_length = &prev_fb->length;

        if ((unsigned long)prev_fb + *prev_fb_length == (unsigned long)new_fb)
        {
            *prev_fb_length = *prev_fb_length + *new_fb_length;
            list_remove(new_elem);
        }
    }

    /*case3 : left is not head and right is not*/
    if (!is_head(prev_elem) && !is_tail(next_elem))
    {
        struct free_block *prev_fb = list_entry (prev_elem, struct free_block, elem);
        size_t *prev_fb_length = &prev_fb->length;
        struct free_block *next_fb = list_entry (next_elem, struct free_block, elem);
        size_t *next_fb_length = &next_fb->length;

        /* new free block can merge with both left and right*/
        if (((unsigned long)new_fb + *new_fb_length == (unsigned long)next_fb)
            && ((unsigned long)prev_fb) + *prev_fb_length == (unsigned long)new_fb)
        {
            *prev_fb_length = *prev_fb_length + *new_fb_length + *next_fb_length;
            list_remove(new_elem);
            list_remove(next_elem);
        }

        /* new free block can only merge with left*/
        if (((unsigned long)prev_fb + *prev_fb_length == (unsigned long)new_fb)
            && ((unsigned long)new_fb + *new_fb_length != (unsigned long)next_fb))
        {
            *prev_fb_length = *prev_fb_length + *new_fb_length;
            list_remove(new_elem);
        }

        /* new free block can only merge with right*/
        if (((unsigned long)new_fb + *new_fb_length == (unsigned long)next_fb)
            && ((unsigned long)prev_fb) + *prev_fb_length != (unsigned long)new_fb)
        {
            *new_fb_length = *new_fb_length + *next_fb_length;
            list_remove(next_elem);
        }
    }
    pthread_mutex_unlock(&gmutex);
    return;
}


/* Return the number of elements in the free list. */
size_t mem_sizeof_free_list(void)
{
    return list_size(&free_list);
}

/* Dump the free list. Output a human readable list of free blocks.
Implementation of this method is optional. */
void mem_dump_free_list(void)
{
    pthread_mutex_lock(&gmutex);
    struct list_elem *e;

    for (e = list_begin(&free_list); e != list_end(&free_list); e = list_next(e))
    {
        struct free_block *fb = list_entry (e, struct free_block, elem);
        size_t *fb_length = &fb->length;
        printf("address length:\n");
        printf("%p %d\n", fb, *fb_length);
    }
    pthread_mutex_unlock(&gmutex);
}


