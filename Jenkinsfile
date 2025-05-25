pipeline {
  agent any

  environment {
    COMPOSE_FILE = 'docker-compose.yml'
  }

  stages {

    stage('停止旧容器') {
      steps {
        echo '🔻 停止正在运行的旧服务...'
        sh 'docker compose down || true'
      }
    }

    stage('构建所有服务镜像') {
      steps {
        echo '🔧 构建镜像中...'
        sh 'docker compose build'
      }
    }

    stage('启动所有服务') {
      steps {
        echo '🚀 启动容器中...'
        sh 'docker compose up -d'
      }
    }

    stage('验证服务状态') {
      steps {
        echo '📋 当前容器状态如下：'
        sh 'docker compose ps'
      }
    }
  }

  post {
    success {
      echo '✅ 一键部署成功！'
    }
    failure {
      echo '❌ 构建失败，请查看 Jenkins 控制台输出日志。'
    }
  }
}
