import time

from itertools import cycle, islice

from concurrent.futures import ThreadPoolExecutor
import numpy as np

from common import prepare_troia_client, save_csv, get_tc

TESTSET = './datasets/10000/'

PACK_SIZES = [10, 50, 100, 500, 1000, 5000, 10000]
JID = "UPLOAD_SPEED_TEST"
THREADS = 10
REPEAT = 5

EXECUTOR = ThreadPoolExecutor(THREADS)
TCS = [get_tc() for _ in xrange(THREADS)]


def split(psize, data):
    return [data[i * psize:(i + 1) * psize] for i in xrange(len(data) / psize)]


def upload_single(package, tc):
    tc.load_worker_assigned_labels(package, JID)


def upload(packages):
    tc = TCS[0]
    for package in packages:
        upload_single(package, tc)


def test_parallel(packages):
    tcs = islice(cycle(TCS), len(packages))
    # map(upload_single, packages, tcs)
    list(EXECUTOR.map(upload_single, packages, tcs))


def test_normal(packages):
    upload(packages)


def timeit(f, *args, **kwargs):
    start = time.time()
    f(*args, **kwargs)
    return time.time() - start


def do_test(fun, splited):
    def tmp():
        prepare_troia_client(JID, TESTSET)
        return timeit(fun, splited)
    times = np.array([tmp() for _ in xrange(REPEAT)])
    return (np.mean(times), np.std(times))


def main():
    def tmp(packsize):
        print "Working on size: " + str(packsize)
        _, inputt = prepare_troia_client(JID, TESTSET)
        splited = split(packsize, inputt)
        synch_time = do_test(test_normal, splited)
        parallel_time = do_test(test_parallel, splited)
        return (packsize, synch_time[0], synch_time[1],
                parallel_time[0], parallel_time[1])

    res = [tmp(packsize) for packsize in PACK_SIZES]
    save_csv('upload_speed.txt', res)


if __name__ == '__main__':
    main()
