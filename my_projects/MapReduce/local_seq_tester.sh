#!/usr/bin/env bash
#   Program:    This shell is used to test local mapreduce sequential work.
#
#   Version:    1.0
#   Author:     Wei Fang
#   Email:  lancefangw@gmail.com 

# seq
#
# python2.7 mr_seq.py word_count.py 3000 3 word_count_test.txt count_all
# python2.7 mr_seq.py bin_hamming_enc.py 3 2 test_data enc_all
# python2.7 mr_seq.py bin_hamming_dec.py 3 2 enc_all dec_all
# python2.7 mr_seq.py bin_hamming_err.py 3 2 enc_all err_all
# python2.7 mr_seq.py bin_hamming_chk.py 3 2 err_all chk_all
# python2.7 mr_seq.py bin_hamming_fix.py 3 2 err_all fix_all

# check
# python2.7 wc_test.py word_count_test.txt count_all
# python2.7 hamming_test.py bin enc test_data test_enc.bin enc_all
# python2.7 hamming_test.py bin dec test_enc.bin test_dec.txt dec_all
# python2.7 hamming_test.py bin err 30 test_enc.bin test_err.bin err_all
# python2.7 hamming_test.py bin chk test_err.bin test_chk.txt chk_all
# python2.7 hamming_test.py bin fix test_err.bin test_fix.bin fix_all


echo "local sequential mapreduce tester"
echo "Select word_count/enc/dec/err/chk/fix/clean/quit"
select option in "word_count" "enc" "dec" "err" "chk" "fix" "clean" "quit"; do
    case $option in
        word_count ) 
            echo "start assign word_count job"
            python2.7 mr_seq.py word_count.py 1000000 3 file_5M.txt count_all
            echo "Test result: word_count"
            python2.7 wc_test.py file_5M.txt count_all
            ;;
        enc )
            echo "start assign hamming encode job"
            python2.7 mr_seq.py bin_hamming_enc.py 60000 3 file_300K.txt enc_all
            echo "Test result: encode"
            python2.7 hamming_test.py bin enc file_300K.txt test_enc.bin enc_all
            ;;
        dec )
            echo "start assign hamming decode job"
            python2.7 mr_seq.py bin_hamming_dec.py 60000 3 enc_all dec_all
            echo "Test result: decode"
            python2.7 hamming_test.py bin dec test_enc.bin test_dec.txt dec_all
            ;;
        err )
            echo "start assign hamming error job"
            python2.7 mr_seq.py bin_hamming_err.py 60000 3 enc_all err_all
            echo "Test result: error"
            python2.7 hamming_test.py bin err 30 test_enc.bin test_err.bin err_all
            ;;
        chk )
            echo "start assign hamming check job"
            python2.7 mr_seq.py bin_hamming_chk.py 60000 3 err_all chk_all
            echo "Test result: check"
            python2.7 hamming_test.py bin chk test_err.bin test_chk.txt chk_all
            ;;
        fix )
            echo "start assign hamming fix job"
            python2.7 mr_seq.py bin_hamming_fix.py 60000 3 err_all fix_all
            echo "Test result: fix"
            python2.7 hamming_test.py bin fix test_err.bin test_fix.bin fix_all
            ;;
        clean ) rm -rf 2015*; rm -rf *.pyc;
                rm test_dec.txt;
                rm test_enc.bin;
                rm test_err.bin;
                rm test_fix.bin;
                rm test_chk.txt;
                rm .pym*;
                rm .pyr*;
                rm *_all;
                echo "Finish to clean all temporary files"
                ;;
        quit ) break;;
        *) echo "Please select from 1:word_count 2.enc 3.dec 4.err 5.chk 6.fix 7. clean 8. quit";;
    esac
done

