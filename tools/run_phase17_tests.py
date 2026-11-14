"""Run Phase 17 tests (RTI + rebaseline + extension) and print results."""
import os, subprocess, sys
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

print("=== Python Phase 17 Tests ===")
r1 = subprocess.run([sys.executable, "-m", "unittest", "discover", "-s", "tools", "-p", "test_phase17*.py", "-v"],
                    cwd=ROOT, capture_output=False)
print(f"\nPython Phase 17 exit code: {r1.returncode}\n")

print("=== Extension Tests (core + assist) ===")
for test_file in ["extension/core.test.mjs", "extension/assist.test.mjs"]:
    r = subprocess.run(["node", "--test", test_file], cwd=ROOT, capture_output=False)
    print(f"{test_file} exit code: {r.returncode}\n")

print("=== Full Python Test Suite ===")
r2 = subprocess.run([sys.executable, "-m", "unittest", "discover", "-s", "tools", "-p", "test_*.py", "-v"],
                    cwd=ROOT, capture_output=False)
print(f"\nFull Python tests exit code: {r2.returncode}")

sys.exit(max(r1.returncode, r2.returncode))
