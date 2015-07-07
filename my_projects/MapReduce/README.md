# README

## MapReduce Project
## Member: yxu66, wfang2

## Sequential Execution:
    1. Split file according to the demands.
    2. Import Map job.
    3. Retrieve exactly input.
    4. Do Map. (For map, there is a dictionary: table contains all map information)
    5. Save into intermediate file. (Use table to output file in order)
    5. Import Reduce job.
    6. Do Reduce.
    7. Save into file by given base name. (Each reducer output the result by itself
        once the work has done)
    8. Collect all result file and merge the result into one file.


## MapReduce Execution:
    1. Activate master, workers. Register workers in workers bookkeeper of master.
    2. Client submits a job to master. Get progress from master every 1s.
    3. Master assigns map and reduce job to mappers and reducers. Record the job information in jobs_tracker of master
    4. If a mapper finishes its job, notify all active reducers to get the intermediate file.
    5. If a reducer is assigned reduce job, check if there is any finished mapper.
       If so, get the intermediate file. After the reducer gets all the intermediate files, do reduce job.
    6. After all the mappers and reducers finish the job, do collect.
    
## Failure Tolerance
    We can handle these failures
    1. worker crash before processing
    2. worker crash in processing
    3. worker crash after processing before collect
    
## Feature
    1. Mapper automatically gets next unit of data from next split (e.g. next line or next word)
    2. Support code shipping 
    3. Handle worker failure
    4. Store Map output in files
    5. Progress report to client
    
## Usage
    Highly Recommended

        1. start master and worker
            local
            python2.7 mr_master.py 25000 ~/USF/CS636/project02
            python2.7 mr_worker.py 0.0.0.0:25000 0.0.0.0:25001
            python2.7 mr_worker.py 0.0.0.0:25000 0.0.0.0:25002
            
            bass (master:bass18  worker: bass17, bass24)
            cd ~/USF/CS636/project02/
            python2.7 mr_master.py 25000 ~/USF/CS636/project02
            python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.217:25001
            python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.224:25002
            
        2.
            sh mapreduce_tester.sh
            sh local_seq_tester.sh

    seq
    
        python2.7 mr_seq.py word_count.py 3000 3 word_count_test.txt count_all
        python2.7 mr_seq.py bin_hamming_enc.py 3 2 test_data enc_all
        python2.7 mr_seq.py bin_hamming_dec.py 3 2 enc_all dec_all
        python2.7 mr_seq.py bin_hamming_err.py 3 2 enc_all err_all
        python2.7 mr_seq.py bin_hamming_chk.py 3 2 err_all chk_all
        python2.7 mr_seq.py bin_hamming_fix.py 3 2 err_all fix_all
    
    master
    
        python2.7 mr_master.py 25000 ~/USF/CS636/project02
    
    worker
    
        python2.7 mr_worker.py 0.0.0.0:25000 0.0.0.0:25001
        python2.7 mr_worker.py 0.0.0.0:25000 0.0.0.0:25002
    
    client
    
        python2.7 mr_job.py 0.0.0.0:25000 word_count.py 3000 1 word_count_test.txt count
        python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_enc.py 3 2 test_data enc
        python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_dec.py 3 2 enc_all dec
        python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_err.py 3 2 enc_all err
        python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_chk.py 3 2 err_all chk
        python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_fix.py 3 2 err_all fix
    
    collect 
    
        python2.7 mr_collect.py count count_all 0.0.0.0:25000
        python2.7 mr_collect.py enc enc_all 0.0.0.0:25000
        python2.7 mr_collect.py dec dec_all 0.0.0.0:25000
        python2.7 mr_collect.py err err_all 0.0.0.0:25000
        python2.7 mr_collect.py chk chk_all 0.0.0.0:25000
        python2.7 mr_collect.py fix fix_all 0.0.0.0:25000
    
    check
        python2.7 wc_test.py word_count_test.txt count_all
        python2.7 hamming_test.py bin enc test_data test_enc.bin enc_all
        python2.7 hamming_test.py bin dec test_enc.bin test_dec.txt dec_all
        python2.7 hamming_test.py bin err 30 test_enc.bin test_err.bin err_all
        python2.7 hamming_test.py bin chk test_err.bin test_chk.txt chk_all
        python2.7 hamming_test.py bin fix test_err.bin test_fix.bin fix_all
    
## Test Flag
    test_failure = True/False : add gevent.sleep(3) between map and combine
                                easy to test failure tolerance when doing map job

## Presentation

ssh wfang2@stargate.cs.usfca.edu
cd ~/USF/CS636/project02/
python2.7 mr_master.py 25000 ~/USF/CS636/project02
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.201:25001
<!-- python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.202:25002 -->
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.203:25003
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.204:25004
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.205:25005
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.206:25006
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.207:25007
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.208:25008
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.209:25009
python2.7 mr_worker.py 10.0.0.218:25000 10.0.0.220:25020

