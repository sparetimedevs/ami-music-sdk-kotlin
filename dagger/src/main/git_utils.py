import dagger
from dagger import dag


class GitClient:

    def container_echo(self, string_arg: str) -> dagger.Container:
        """Returns a container that echoes whatever string argument is provided"""
        return dag.container().from_("alpine:latest").with_exec(["echo", string_arg])

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
