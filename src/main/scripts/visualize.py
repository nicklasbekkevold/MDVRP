import sys

import matplotlib.pyplot as plt
import numpy as np

training_data_path = '../resources/training_data.txt'
plot_path = '../resources/training_data.jpg'

generations, best_durations, average_durations = np.loadtxt(training_data_path, unpack=True)

plt.title('Training data')
plt.xlabel('generation')
plt.ylabel('duration')

plt.plot(generations, average_durations, label='Average duration', color='k')
plt.plot(generations, best_durations, label='Best duration', color='deepskyblue')

# Plot benchmarks if benchmark file specified
if len(sys.argv) > 1:
    benchmark_path = f'../resources/benchmark_files/{sys.argv[1]}.res'
    with open(benchmark_path, 'r') as f:
        best_solution = float(f.readline().strip())

    for percentile in [0.0, 0.05, 0.1, 0.2, 0.3]:
        generations.fill(best_solution * (1 + percentile))
        plt.plot(generations, label=str(percentile))

plt.legend()
plt.savefig(plot_path)
plt.close()
