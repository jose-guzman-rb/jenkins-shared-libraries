def call(String project, gox = false) {
    dockerLogin()
    sh "docker image push vfarcic/${project}:latest"
    sh "docker image push vfarcic/${project}:${currentBuild.displayName}"
    sh "docker image push vfarcic/${project}-docs:latest"
    sh "docker image push vfarcic/${project}-docs:${currentBuild.displayName}"
    dockerLogout()
    script {
        if (gox) {
            sh 'docker container run --rm -it -v $PWD:/src vfarcic/gox docker-flow-proxy'
            withCredentials([usernamePassword(credentialsId: "github-token", variable: "GIHBUT_TOKEN")]) {
                def msg = sh(returnStdout: true, script: "git log --format=%B -1").trim()
                sh "docker container run --rm -it -e GITHUB_TOKEN=${GITHUB_TOKEN} -v ${pwd}:/src -w /src vfarcic/github-release git tag -a ${currentBuild.displayName} -m '${msg}'"
            }
        }
    }
}