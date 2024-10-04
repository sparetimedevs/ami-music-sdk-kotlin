"""A generated module for AmiMusicSdkKotlin functions

This module has been generated via dagger init and serves as a reference to
basic module structure as you get started with Dagger.

Two functions have been pre-created. You can modify, delete, or add to them,
as needed. They demonstrate usage of arguments and return types using simple
echo and grep commands. The functions can be called from the dagger CLI or
from one of the SDKs.

The first line in this comment block is a short description line and the
rest is a long description with more detail on the module's purpose or usage,
if appropriate. All modules should have a short description.
"""

# NOTE: it's recommended to move your code into other files in this package
# and keep __init__.py for imports only, according to Python's convention.
# The only requirement is that Dagger needs to be able to import a package
# called "main" (i.e., src/main/).
#
# For example, to import from src/main/main.py:
# >>> from .main import AmiMusicSdkKotlin as AmiMusicSdkKotlin

import dagger
from dagger import dag, function, object_type


@object_type
class AmiMusicSdkKotlin:
    @function
    def container_echo(self, string_arg: str) -> dagger.Container:
        """Returns a container that echoes whatever string argument is provided"""
        return dag.container().from_("alpine:latest").with_exec(["echo", string_arg])

    @function
    async def grep_dir(self, directory_arg: dagger.Directory, pattern: str) -> str:
        """Returns lines that match a pattern in the files of the provided Directory"""
        return await (
            dag.container()
            .from_("alpine:latest")
            .with_mounted_directory("/mnt", directory_arg)
            .with_workdir("/mnt")
            .with_exec(["grep", "-R", pattern, "."])
            .stdout()
        )

    @function
    async def git_log(self, directory_arg: dagger.Directory) -> str:
        """Returns result of git log"""
        return await (
            dag.container()
            .from_("alpine/git:v2.45.2")
            .with_mounted_directory("/mnt", directory_arg)
            .with_workdir("/mnt")
            .with_exec(["git", "log", "-n", "10", "--pretty=format:\"Commit %h - %an, %ar : %s\""])
            .stdout()
        )

    @function
    async def git_log_bot(self, directory_arg: dagger.Directory) -> str:
        """Returns result of git log for bot"""
        a = await self.git_log(directory_arg)
        bot_count=a.count('[bot]')
        return "the amount of [bot] commits in last 10 commits is: " + str(bot_count)