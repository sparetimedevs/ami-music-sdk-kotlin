from main.something import funcy


def func(x):
    return x + 1


def test_answer():
    assert func(4) == 5


def test_answer_funcy():
    assert funcy(3) == 5
