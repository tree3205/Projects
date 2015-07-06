#!/usr/bin/env python
#
# find_links.py - find the href links in html files

import sys

def parse_bycount(args):
    """Find -bycount in command line arguments."""

    new_args = []
    bycount = False

    for arg in args:
        if arg[0] == '-':
            if arg == '-bycount':
                bycount = True
                continue
            else:
                raise Exception('Argument %s not recognized.' % (arg))
        new_args.append(arg)

    return (bycount, new_args)


def get_links_from_text(text, links_dict):
    end = len(text)
    pos = 0
    anchor_begin_open = '<a '
    href = 'href'
    state = 'BEGIN'

    while pos <= end:

        if state == 'BEGIN':
            found_aopen = False
            while pos < end:
                if text[pos:pos+2] in ['<a', '<A']:
                    pos_begin = pos
                    pos += 2
                    found_aopen = True
                    break
                pos += 1
            if not found_aopen:
                break

            found_aclose = False
            while pos < end:
                if text[pos:pos+4] in ['</a>', '</A>']:
                    pos_end = pos
                    pos += 4
                    found_aclose = True
                    break
                pos += 1
            if not found_aclose:
                break

            pos = pos_begin + len(anchor_begin_open)
            state = 'HREF'

        if state == 'HREF':
            # find href attribute
            found_href = False
            while pos < pos_end:
                if text[pos:pos+4].lower() == 'href':
                    pos_href = pos
                    pos += 4
                    found_href = True
                    break
                pos += 1
            if not found_href:
                state = 'BEGIN'
                continue

            pos = pos_href + len(href)

            # Find href =
            while text[pos].isspace():
                pos += 1
            if text[pos] != '=':
                break
            pos += 1
            # Find href begin quote
            while text[pos].isspace():
                pos += 1
            if text[pos] not in ['"', "'"]:
                break
            quote = text[pos]
            pos += 1               
            pos_end_quote = text.find(quote, pos, pos_end)
            if pos_end_quote == -1:
                # No end quote found
                break
            # Get link
            link = text[pos:pos_end_quote]
            # Update links_dict
            link = link.strip()
            if link[0:4] == 'http':
                if link in links_dict:
                    links_dict[link] += 1
                else:
                    links_dict[link] = 1
            state = 'BEGIN'
            continue


def get_links_from_file(file, links_dict):
    """Get the links from a single HTML file."""

    f = open(file, 'r')
    lines = []
    for line in f.readlines():
        lines.append(line.rstrip('\n'))
    f.close()

    text = ''.join(lines)

    get_links_from_text(text, links_dict)
    return 


def get_sorted_links(links_dict, bycount=False):
    links = links_dict.items()

    def bycount_cmp(x, y):
        """Sort by count, then alphabetical by link name"""

        if x[1] == y[1]:
            if x == y:
                return 0
            elif x < y:
                return -1
            else:
                return 1
        else:
            return y[1] - x[1]

    if bycount:
        links.sort(cmp=bycount_cmp)
    else:
        links.sort(key=lambda x: x[0])
    return links


def get_links(files, bycount=False):
    """Return a sorted list of link tuples (link, count).

    The sort order is either alphabetical by link or if bycount is
    True, then by link count order.
    """

    links_dict = {}
    for file in files:
        get_links_from_file(file, links_dict)

    links = get_sorted_links(links_dict, bycount)
    return links


def print_links(links):
    for link in links:
        print '"%s",%d' % (link[0], link[1])


if __name__ == '__main__':
    args = sys.argv[1:]
    bycount, args = parse_bycount(args)

    links = get_links(args, bycount)

    print_links(links)
