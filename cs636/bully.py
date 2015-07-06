
# bully.py - leader election server using the Bully Algorithm
#
# To run this code you need to install ZeroMQ, Gevent, and ZeroRPC
#
# http://zeromq.org
# http://www.gevent.org
# http://zerorpc.dotcloud.com
# 
# These will require some additional libraries
# In order to run an election you need to create a config file:
#
# $ cat config
# 127.0.0.1:9000
# 127.0.0.1:9001
# 127.0.0.1:9002
#
# Then in three separate terminal windows, you run:
#
# $ python bully.py 127.0.0.1:9000
#
# $ python bully.py 127.0.0.1:9001
#
# $ python bully.py 127.0.0.1:9002
#
# You can kill (CTRL-C) a server to see the election run again.

import logging
import sys

import gevent
import zerorpc


STATE_DOWN     = 'DOWN'
STATE_ELECT    = 'ELECT'
STATE_REORG    = 'REORG'
STATE_NORMAL   = 'NORMAL'


class BullyElection(object):

    def __init__(self, addr, config_file='config'):
        self.addr = addr

        # State vector for this server
        self.s = STATE_DOWN
        self.c = 0
        self.h = None
        self.d = 0
        self.up = set()

        self.servers = []
        f = open(config_file, 'r')
        for line in f.readlines():
            line = line.rstrip()
            self.servers.append(line)
        print 'My addr: %s' % (self.addr)
        print 'Server list: %s' % (str(self.servers))

        self.n = len(self.servers)

        self.connections = []

        for i, server in enumerate(self.servers):
            if server == self.addr:
                self.i = i
                self.connections.append(self)
            else:
                c = zerorpc.Client(timeout=1)
                c.connect('tcp://' + server)
                self.connections.append(c)

    def start(self):
        self.check_greenlet = gevent.Greenlet.spawn(self.check)
        self.start_procs()

    def start_procs(self):
        self.pool = gevent.pool.Group()
        self.recovery_greenlet = self.pool.spawn(self.recovery)

    def stop(self):
        self.pool.kill()

    def are_you_there(self):
        logging.debug('[%s] are_you_there() called', self.addr)
        return True

    def are_you_normal(self):
        logging.debug('[%s] are_you_normal() called', self.addr)
        return self.s == STATE_NORMAL

    def halt(self, j):
        logging.debug('[%s] halt() called', self.addr)
        self.s = STATE_ELECT
        self.h = j
        self.stop()

    def new_coordinator(self, j):
        if self.h == j and self.s == STATE_ELECT:
            self.c = j
            self.s = STATE_REORG

    def ready(self, j, x):
        if self.c == j and self.s == STATE_REORG:
            self.d = x
            self.s = STATE_NORMAL

    def election(self):
        self.pool.spawn(self.election_alg).join()

    def election_alg(self):

        logging.debug('[%s] starting election', self.addr)
        # Check to see if any higher priority nodes are up
        for j in range(self.i + 1, self.n):
            try:
                logging.debug('[%s] are_you_there(%d)', self.addr, j)
                self.connections[j].are_you_there()
            except zerorpc.TimeoutExpired:
                continue
            return

        # Trying to become leader
        # Halt all lower priority nodes, starting with this node
        cur = gevent.getcurrent()
        # kill all greenlets except this one.
        for g in self.pool:
            if g is not cur:
                g.kill()
        self.s = STATE_ELECT
        self.h = self.i
        self.up = set()
        for j in range(0, self.i):
            try:
                self.connections[j].halt(self.i)
            except zerorpc.TimeoutExpired:
                continue
            self.up = self.up.union(set([j]))

        # This node has reached its election point.
        # Now inform nodes of new coordinator
        self.c = self.i
        self.s = STATE_REORG
        for j in self.up:
            try:
                self.connections[j].new_coordinator(self.i)
            except zerorpc.TimeoutExpired:
                self.election()
                return

        # Reorganization
        d = self.d + 1
        for j in self.up:
            try:
                self.connections[j].ready(self.i, d)
            except zerorpc.TimeoutExpired:
                return
        self.s = STATE_NORMAL
        print 'Server %s is the leader' % (self.addr)
        logging.info('[%s] is the LEADER', self.addr)

    def recovery(self):
        self.h = None
        self.election()

    def check(self):
        # Called periodically
        # Combines check and timeout calls

        while True:
            gevent.sleep(3)
            
            # Check
            logging.debug('[%s] s = %s, c = %s', self.addr, self.s, self.c)
            logging.info('[%s] check servers' % (self.addr))
            if self.s == STATE_NORMAL and self.c == self.i:
                for j in set(range(self.n)).difference(set([self.i])):
                    try:
                        ans = self.connections[j].are_you_normal()
                    except zerorpc.TimeoutExpired:
                        continue
                    if not ans:
                        self.election()
                        return

            # Timeout
            logging.info('[%s] timeout called', self.addr)
            if self.c != self.i:
                if self.s == STATE_NORMAL or self.s == STATE_REORG:
                    try:
                        self.connections[self.c].are_you_there()
                    except zerorpc.TimeoutExpired:
                        self.election()
                else:
                    self.election()

    def check_servers(self):
        while True:
            gevent.sleep(2)
            for i, server in enumerate(self.servers):
                print '%s : are_you_there = %s' % (server, self.connections[i].are_you_there())


if __name__ == '__main__':
    
    logging.basicConfig(level=logging.INFO)
    
    addr = sys.argv[1]
    be = BullyElection(addr)
    s = zerorpc.Server(be)
    s.bind('tcp://' + addr)
    be.start()
    # Start server
    logging.debug('[%s] Starting ZeroRPC Server' % addr)
    s.run()
