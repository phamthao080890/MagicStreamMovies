#!/bin/bash

# Quick verification script to check if you're ready to load test

echo "================================================"
echo "Load Testing Readiness Check"
echo "================================================"
echo ""

# Check if k6 is installed
echo -n "1. Checking k6 installation... "
if command -v k6 &> /dev/null; then
    echo "✅ INSTALLED ($(k6 version 2>&1 | head -1))"
else
    echo "❌ NOT INSTALLED"
    echo "   Install with: brew install k6"
fi

# Check if Python 3 is available
echo -n "2. Checking Python 3... "
if command -v python3 &> /dev/null; then
    echo "✅ AVAILABLE ($(python3 --version))"
else
    echo "❌ NOT AVAILABLE"
fi

# Check if API is running
echo -n "3. Checking API at http://localhost:8080... "
if curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/movies" 2>/dev/null | grep -q "200\|404"; then
    echo "✅ RUNNING"
else
    echo "❌ NOT RUNNING"
    echo "   Start with: cd ../.. && mvn spring-boot:run"
fi

# Check if MongoDB is running (optional)
echo -n "4. Checking MongoDB... "
if pgrep -x "mongod" > /dev/null; then
    echo "✅ RUNNING"
else
    echo "⚠️  NOT DETECTED (optional check)"
fi

# Check test files
echo -n "5. Checking test files... "
if [ -f "k6-load-test.js" ] && [ -f "k6-smoke-test.js" ]; then
    echo "✅ FOUND"
else
    echo "❌ MISSING"
fi

echo ""
echo "================================================"
echo "Summary"
echo "================================================"

# Count checks
checks_passed=0

command -v k6 &> /dev/null && ((checks_passed++))
command -v python3 &> /dev/null && ((checks_passed++))
curl -s -o /dev/null "http://localhost:8080/api/movies" 2>/dev/null && ((checks_passed++))
[ -f "k6-load-test.js" ] && ((checks_passed++))

echo "Checks passed: $checks_passed/4"
echo ""

if [ $checks_passed -eq 4 ]; then
    echo "✅ You're ready to run load tests!"
    echo ""
    echo "Quick start:"
    echo "  k6 run k6-smoke-test.js"
elif [ $checks_passed -ge 2 ]; then
    echo "⚠️  Almost ready! Fix the items marked with ❌ above"
else
    echo "❌ Not ready yet. Please:"
    echo "  1. Install k6: brew install k6"
    echo "  2. Start the API: mvn spring-boot:run"
fi

echo ""

