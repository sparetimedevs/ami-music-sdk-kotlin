import dagger
from main.git_utils import GitClient


class SomethingGit:

    def __init__(self, git_client: GitClient):
        self.git_client = git_client

    async def git_log_bot(self, directory_arg: dagger.Directory) -> str:
        """Returns result of git log for bot"""
        last_10_commits = await self.git_client.git_log(directory_arg)
        print("THE BEGINNING OF THE last_10_commits")
        print(last_10_commits)
        print("THE END OF THE last_10_commits")
        bot_count = last_10_commits.count('[bot]')
        return "the amount of [bot] commits in last 10 commits is: " + str(bot_count)
