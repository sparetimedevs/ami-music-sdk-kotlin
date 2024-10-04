import pytest

import dagger
from main.something_git import SomethingGit


@pytest.mark.asyncio
async def test_git_log_bot():
    directory_arg = dagger.Directory("ok")
    a1 = await SomethingGit(GitClientStub()).git_log_bot(directory_arg)
    assert a1 == "the amount of [bot] commits in last 10 commits is: 4"


class GitClientStub:
    async def git_log(self, directory_arg: dagger.Directory) -> str:
        return """"Commit 4c5ab73 - Sam, 5 hours ago : Add unit tests."
        "Commit 50313ac - Sam, 7 hours ago : Add log statements to Dagger.io function."
        "Commit 862f778 - Sam, 7 hours ago : Add more Dagger.io functions."
        "Commit 89431ea - Sam, 8 hours ago : Add Dagger.io."
        "Commit 50374ee - dependabot[bot], 7 days ago : Bump org.jetbrains.kotlinx:kotlinx-serialization-json (#11)"
        "Commit 4f89b29 - Pippin, 7 days ago : Improve validation (#12)"
        "Commit 782caaa - dependabot[bot], 7 weeks ago : Bump kotlin-version from 2.0.0 to 2.0.10 (#8)"
        "Commit 74e3e62 - dependabot[bot], 3 months ago : Bump org.jetbrains.kotlinx:kotlinx-serialization-json (#7)"
        "Commit d23cd7a - dependabot[bot], 3 months ago : Bump kotest-version from 5.9.0 to 5.9.1 (#5)"
        "Commit 245ca7e - Merry, 4 months ago : Add option to run compatibility tests as a separate task. (#4)" """
