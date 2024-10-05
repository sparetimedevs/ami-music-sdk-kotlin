import dagger
from dagger import dag


class Build:
    async def build(self, src: dagger.Directory) -> str:
        # build app
        builder = (
            dag.container()
            .from_("eclipse-temurin:11.0.24_8-jdk")
            .with_mounted_directory("/mnt", src)
            .with_workdir("/mnt")
            .with_exec(["./gradlew", "clean", "build", "-x", "allTests", "-x", "jsBrowserTest"])
            # not sure why -x allTests is not enough
        )

        # publish binary on alpine base
        unit_tester = (
            builder
            .with_exec(["./gradlew", "allTests"])
        )

        return await unit_tester.stdout()
