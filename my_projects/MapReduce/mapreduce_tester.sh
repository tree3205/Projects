#!/usr/bin/env bash
#   Program:    This shell is used to test mapreduce.
#
#   Version:    1.0
#   Author:     Wei Fang
#   Email:  lancefangw@gmail.com 

# master

#     python2.7 mr_master.py 25000 ~/USF/CS636/project02

# worker

#     python2.7 mr_worker.py 0.0.0.0:25000 0.0.0.0:25001
#     python2.7 mr_worker.py 0.0.0.0:25000 0.0.0.0:25002

# client

# python2.7 mr_job.py 0.0.0.0:25000 word_count.py 300000 3 file_5M.txt count
# python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_enc.py 300000 2 file_5M.txt enc
# python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_enc.py 60000 3 file_300K.txt enc
# python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_dec.py 300000 2 enc_all dec
# python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_err.py 300000 2 enc_all err
# python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_chk.py 300000 2 err_all chk
# python2.7 mr_job.py 0.0.0.0:25000 bin_hamming_fix.py 300000 2 err_all fix

# collect 

# python2.7 mr_collect.py count count_all 0.0.0.0:25000
# python2.7 mr_collect.py enc enc_all 0.0.0.0:25000
# python2.7 mr_collect.py dec dec_all 0.0.0.0:25000
# python2.7 mr_collect.py err err_all 0.0.0.0:25000
# python2.7 mr_collect.py chk chk_all 0.0.0.0:25000
# python2.7 mr_collect.py fix fix_all 0.0.0.0:25000

# check
# python2.7 wc_test.py word_count_test.txt count_all
# python2.7 hamming_test.py bin enc file_5M.txt test_enc.bin enc_all
# python2.7 hamming_test.py bin dec test_enc.bin test_dec.txt dec_all
# python2.7 hamming_test.py bin err 30 test_enc.bin test_err.bin err_all
# python2.7 hamming_test.py bin chk test_err.bin test_chk.txt chk_all
# python2.7 hamming_test.py bin fix test_err.bin test_fix.bin fix_all


echo "Please input master ip_port.   default: 0.0.0.0:25000"
read master_ip_port
if [ -z ${master_ip_port} ]; then
    master_ip_port="0.0.0.0:25000"
fi
echo "Master ip_port is ${master_ip_port}"
echo "mapreduce tester"
echo "Select word_count/enc/dec/err/chk/fix/clean/quit"
select option in "word_count" "enc" "dec" "err" "chk" "fix" "clean" "quit"; do
    case $option in
        word_count ) 
            echo "start assign word_count job"
            python2.7 mr_job.py ${master_ip_port} word_count.py 1000000 3 file_5M.txt count
            echo
            echo "Press Enter to continue"
            read
            echo "start collect"
            python2.7 mr_collect.py count count_all ${master_ip_port}
            echo
            echo "Press Enter to continue"
            read
            echo "Test result"
            python2.7 wc_test.py file_5M.txt count_all
            ;;
        enc )
            echo "start assign hamming encode job"
            python2.7 mr_job.py ${master_ip_port} bin_hamming_enc.py 60000 3 file_300K.txt enc
            echo
            echo "Press Enter to continue"
            read
            echo "start collect"
            python2.7 mr_collect.py enc enc_all ${master_ip_port}
            echo
            echo "Press Enter to continue"
            read
            echo "Test result"
            python2.7 hamming_test.py bin enc file_300K.txt test_enc.bin enc_all
            ;;
        dec )
            echo "start assign hamming decode job"
            python2.7 mr_job.py ${master_ip_port} bin_hamming_dec.py 60000 3 enc_all dec
            echo
            echo "Press Enter to continue"
            read
            echo "start collect"
            python2.7 mr_collect.py dec dec_all ${master_ip_port}
            echo
            echo "Press Enter to continue"
            read
            echo "Test result"
            python2.7 hamming_test.py bin dec test_enc.bin test_dec.txt dec_all
            ;;
        err )
            echo "start assign hamming error job"
            python2.7 mr_job.py ${master_ip_port} bin_hamming_err.py 60000 3 enc_all err
            echo
            echo "Press Enter to continue"
            read
            echo "start collect"
            python2.7 mr_collect.py err err_all ${master_ip_port}
            echo "Press Enter to continue"
            read
            echo "Test result"
            python2.7 hamming_test.py bin err 30 test_enc.bin test_err.bin err_all
            ;;
        chk )
            echo "start assign hamming check job"
            python2.7 mr_job.py ${master_ip_port} bin_hamming_chk.py 60000 3 err_all chk
            echo
            echo "Press Enter to continue"
            read
            echo "start collect"
            python2.7 mr_collect.py chk chk_all ${master_ip_port}
            echo
            echo "Press Enter to continue"
            read
            echo "Test result"
            python2.7 hamming_test.py bin chk test_err.bin test_chk.txt chk_all
            ;;
        fix )
            echo "start assign hamming fix job"
            python2.7 mr_job.py ${master_ip_port} bin_hamming_fix.py 60000 3 err_all fix
            echo
            echo "Press Enter to continue"
            read
            echo "start collect"
            python2.7 mr_collect.py fix fix_all ${master_ip_port}
            echo
            echo "Press Enter to continue"
            read
            echo "Test result"
            python2.7 hamming_test.py bin fix test_err.bin test_fix.bin fix_all
            ;;
        clean ) rm -rf 2015*; rm -rf *.pyc;
                rm test_dec.txt
                rm test_enc.bin
                rm test_err.bin
                rm test_fix.bin
                rm test_chk.txt
                rm dec_*
                rm enc_*
                rm fix_*
                rm chk_*
                rm err_*
                rm *_all
                echo "Finish to clean all temporary files"
                ;;
        quit ) break;;
        *) echo "Please select from 1:word_count 2.enc 3.dec 4.err 5.chk 6.fix 7. clean 8. quit";;
    esac
done

