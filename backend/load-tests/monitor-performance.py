#!/usr/bin/env python3
"""
Simple performance monitoring script for the Streaming API
Monitors response times and throughput in real-time
"""

import requests
import time
import statistics
from datetime import datetime
from collections import deque

class APIPerformanceMonitor:
    def __init__(self, base_url="http://localhost:8080/api", duration=60):
        self.base_url = base_url
        self.duration = duration
        self.response_times = deque(maxlen=1000)
        self.errors = 0
        self.total_requests = 0

    def test_endpoint(self, endpoint):
        """Test a single endpoint and record metrics"""
        start_time = time.time()
        try:
            response = requests.get(f"{self.base_url}{endpoint}", timeout=5)
            elapsed = (time.time() - start_time) * 1000  # Convert to ms

            self.response_times.append(elapsed)
            self.total_requests += 1

            if response.status_code != 200:
                self.errors += 1
                return False
            return True
        except Exception as e:
            self.errors += 1
            self.total_requests += 1
            return False

    def print_stats(self):
        """Print current statistics"""
        if not self.response_times:
            print("No data collected yet...")
            return

        avg_response = statistics.mean(self.response_times)
        min_response = min(self.response_times)
        max_response = max(self.response_times)

        # Calculate p95 and p99
        sorted_times = sorted(self.response_times)
        p95_idx = int(len(sorted_times) * 0.95)
        p99_idx = int(len(sorted_times) * 0.99)
        p95 = sorted_times[p95_idx] if p95_idx < len(sorted_times) else sorted_times[-1]
        p99 = sorted_times[p99_idx] if p99_idx < len(sorted_times) else sorted_times[-1]

        error_rate = (self.errors / self.total_requests * 100) if self.total_requests > 0 else 0

        print(f"\n{'='*60}")
        print(f"Performance Metrics - {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print(f"{'='*60}")
        print(f"Total Requests:    {self.total_requests}")
        print(f"Errors:           {self.errors} ({error_rate:.2f}%)")
        print(f"Avg Response:     {avg_response:.2f} ms")
        print(f"Min Response:     {min_response:.2f} ms")
        print(f"Max Response:     {max_response:.2f} ms")
        print(f"P95 Response:     {p95:.2f} ms")
        print(f"P99 Response:     {p99:.2f} ms")
        print(f"{'='*60}\n")

    def run(self):
        """Run the monitoring test"""
        print(f"Starting performance monitoring for {self.duration} seconds...")
        print(f"Base URL: {self.base_url}")

        endpoints = [
            "/movies",
            "/movies/search?q=test",
            "/users",
            "/movies/genre/Action",
        ]

        start = time.time()
        last_print = start

        while time.time() - start < self.duration:
            # Test each endpoint
            for endpoint in endpoints:
                self.test_endpoint(endpoint)
                time.sleep(0.1)  # Small delay between requests

            # Print stats every 10 seconds
            if time.time() - last_print >= 10:
                self.print_stats()
                last_print = time.time()

        # Final stats
        print("\nFinal Results:")
        self.print_stats()

        # Performance assessment
        avg_response = statistics.mean(self.response_times)
        error_rate = (self.errors / self.total_requests * 100) if self.total_requests > 0 else 0

        print("Performance Assessment:")
        if avg_response < 100 and error_rate < 1:
            print("✅ EXCELLENT - System performing very well!")
        elif avg_response < 300 and error_rate < 5:
            print("✓ GOOD - Acceptable performance")
        elif avg_response < 500 and error_rate < 10:
            print("⚠ FAIR - Consider optimization")
        else:
            print("❌ POOR - Immediate optimization required")

if __name__ == "__main__":
    import sys

    duration = 60  # Default 60 seconds
    if len(sys.argv) > 1:
        duration = int(sys.argv[1])

    print("Streaming API Performance Monitor")
    print("Usage: python3 monitor-performance.py [duration_in_seconds]")
    print("")

    monitor = APIPerformanceMonitor(duration=duration)
    try:
        monitor.run()
    except KeyboardInterrupt:
        print("\n\nMonitoring stopped by user")
        monitor.print_stats()

