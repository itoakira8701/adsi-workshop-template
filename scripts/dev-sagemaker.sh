#!/bin/bash
set -e

cd "$(dirname "$0")/.."

export SAGEMAKER=1
export NEXT_PUBLIC_BASE_PATH="/codeeditor/default/absports/3000"

# Stop existing processes on relevant ports
for port in 3000 3001; do
  pid=$(cat /proc/net/tcp6 2>/dev/null | awk -v p=$(printf '%04X' $port) '$2 ~ ":"p {split($0,a," "); print a[8]}' | head -1)
  [ -n "$pid" ] && [ "$pid" != "0" ] && kill "$pid" 2>/dev/null || true
done
sleep 1

echo "=== Building frontend (SAGEMAKER=1) ==="
cd frontend
npm run build

echo "=== Starting backend (8080) ==="
cd ../backend
./gradlew bootRun > /tmp/backend.log 2>&1 &

echo "=== Starting Next.js (3001) ==="
cd ../frontend
SAGEMAKER=1 npx next start -H 127.0.0.1 -p 3001 > /tmp/next.log 2>&1 &
sleep 3

echo "=== Starting SageMaker proxy (3000) ==="
node /home/sagemaker-user/adsi-workshop-template/frontend/scripts/sagemaker-proxy.mjs > /tmp/proxy.log 2>&1 &
sleep 1

echo ""
echo "=== Ready ==="
echo "1. PORTS tab → globe button for port 3000"
echo "2. Replace 'ports' with 'absports' in the URL"
echo "3. Login: admin / admin123"
