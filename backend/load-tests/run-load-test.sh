#!/bin/bash

# Streaming API Load Test Runner Script
# This script helps you run different load tests easily

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
API_URL="http://localhost:8080"
REPORT_DIR="./reports"

echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}Streaming API Load Test Runner${NC}"
echo -e "${GREEN}=====================================${NC}"
echo ""

# Check if API is running
check_api() {
    echo -e "${YELLOW}Checking if API is running...${NC}"
    if curl -s -o /dev/null -w "%{http_code}" "$API_URL/api/movies" | grep -q "200\|404"; then
        echo -e "${GREEN}✓ API is running${NC}"
        return 0
    else
        echo -e "${RED}✗ API is not running. Please start it first:${NC}"
        echo -e "  cd /project_streamingvideo/backend"
        echo -e "  mvn spring-boot:run"
        exit 1
    fi
}

# Create reports directory
mkdir -p "$REPORT_DIR"

# Menu
echo "Select a test to run:"
echo "1) Basic Load Test (k6)"
echo "2) Stress Test (k6)"
echo "3) Spike Test (k6)"
echo "4) Artillery Load Test"
echo "5) Quick Smoke Test"
echo "6) Run All Tests"
echo "7) Exit"
echo ""
read -p "Enter your choice [1-7]: " choice

case $choice in
    1)
        check_api
        echo -e "${GREEN}Running Basic Load Test...${NC}"
        k6 run --out json="$REPORT_DIR/load-test-$(date +%Y%m%d-%H%M%S).json" k6-load-test.js
        ;;
    2)
        check_api
        echo -e "${GREEN}Running Stress Test...${NC}"
        k6 run --out json="$REPORT_DIR/stress-test-$(date +%Y%m%d-%H%M%S).json" k6-stress-test.js
        ;;
    3)
        check_api
        echo -e "${GREEN}Running Spike Test...${NC}"
        k6 run --out json="$REPORT_DIR/spike-test-$(date +%Y%m%d-%H%M%S).json" k6-spike-test.js
        ;;
    4)
        check_api
        echo -e "${GREEN}Running Artillery Load Test...${NC}"
        artillery run --output "$REPORT_DIR/artillery-$(date +%Y%m%d-%H%M%S).json" artillery-load-test.yml
        ;;
    5)
        check_api
        echo -e "${GREEN}Running Quick Smoke Test...${NC}"
        k6 run --vus 10 --duration 30s k6-load-test.js
        ;;
    6)
        check_api
        echo -e "${GREEN}Running All Tests...${NC}"
        echo -e "${YELLOW}This will take approximately 30-40 minutes${NC}"
        read -p "Continue? [y/N]: " confirm
        if [[ $confirm == [yY] ]]; then
            k6 run --out json="$REPORT_DIR/load-test-$(date +%Y%m%d-%H%M%S).json" k6-load-test.js
            sleep 60  # Cool down period
            k6 run --out json="$REPORT_DIR/stress-test-$(date +%Y%m%d-%H%M%S).json" k6-stress-test.js
            sleep 60
            k6 run --out json="$REPORT_DIR/spike-test-$(date +%Y%m%d-%H%M%S).json" k6-spike-test.js
            echo -e "${GREEN}All tests completed!${NC}"
        fi
        ;;
    7)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo -e "${RED}Invalid choice${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}Test Completed!${NC}"
echo -e "${GREEN}=====================================${NC}"
echo ""
echo -e "Reports saved in: ${YELLOW}$REPORT_DIR${NC}"
echo ""
echo "Key Metrics to Check:"
echo "  - Response Time (p95, p99)"
echo "  - Error Rate"
echo "  - Throughput (requests/sec)"
echo "  - Resource Usage (CPU, Memory)"
echo ""

