#!/bin/bash
# 一键式环境安装脚本

# 主机ip
HOST=192.168.32.32
# 密码
PASSWORD=qepwq

use() {
  echo "use:$0 {install|uninstall|restore}"
}

install() {
  echo "[ start installing ]"
  
  sed -i 's/${host}/'$HOST'/g' *.yml */*.yml */*.properties
  sed -i 's/${password}/'$PASSWORD'/g' *.yml */*.properties */*.conf
  
  docker-compose -f kiligz.yml up -d
  
  sleep 120
  
  curl -X POST "http://$HOST:8848/nacos/v1/console/namespaces" -d "customNamespaceId=seata&namespaceName=seata&namespaceDesc=seata"
  curl -X POST "http://$HOST:8848/nacos/v1/console/namespaces" -d "customNamespaceId=dubbo&namespaceName=dubbo&namespaceDesc=dubbo"
  
  CONTENT="tenant=seata&dataId=seataServer.properties&group=SEATA_GROUP&content=`cat seata/seataServer.properties`&type=properties"
  curl -X POST "http://$HOST:8848/nacos/v1/cs/configs" -d "$CONTENT"
  
  docker-compose -f kiligz-seata.yml up -d
  
  echo "[ install finish ]"
  echo ""
  echo "[                modules              ]"
  echo "[ mysql: $HOST:3306 root/$PASSWORD ]"
  echo "[ nacos: $HOST:8848/nacos nacos/nacos ]"
  echo "[ xxl-job-admin: $HOST:8082/xxl-job-admin admin/123456 ]"
  echo "[ canal-server: $HOST:11111 ]"
  echo "[ sentinel-dashboard: $HOST:8858 sentinel/sentinel ]"
  echo "[ rocketmq: $HOST:8081 admin/admin 9876 ]"
  echo "[ seata: $HOST:7091 seata/seata 8091]"
  echo "[ redis: $HOST:6379 $PASSWORD ]"
}

uninstall() {
  echo "[ start uninstalling ]"
  docker-compose -f kiligz-seata.yml down
  docker-compose -f kiligz.yml down
  echo "[ uninstall finish ]"
}

restore() {
  echo "[ start restoring ]"
  sed -i 's/'$HOST'/${host}/g' *.yml */*.yml */*.properties
  sed -i 's/'$PASSWORD'/${password}/g' *.yml */*.properties */*.conf
}

case $1 in
install)
  install
  ;;
uninstall)
  uninstall
  ;;
restore)
  restore
  ;;
*)
  use
  ;;
esac
exit 0
