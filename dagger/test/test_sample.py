import pytest

import dagger
from lol234 import AmiMusicSdkKotlin
from lol234.something import funcy


def func(x):
    return x + 1


def test_answer():
    assert func(3) == 5


def test_answer_2():
    assert func(4) == 5


def test_answer_3():
    assert funcy(3) == 5


@pytest.mark.asyncio
async def test_git_log_bot():
    directory_arg = dagger.Directory("ok")
    a1 = await AmiMusicSdkKotlin().git_log_bot(directory_arg)
    print(a1)
    a = 'b'
    assert a == 'b'
