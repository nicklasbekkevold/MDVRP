import matplotlib.pyplot as plt
import numpy as np

training_data_path = "../resources/training_data.txt"
plot_path = "../resources/training_data.jpg"

generations, best_durations, average_durations = np.loadtxt(training_data_path, unpack=True)

plt.title('Training data')
plt.xlabel('generation')
plt.ylabel('duration')

plt.plot(generations, average_durations, label='Average duration', color='tab:blue')
plt.plot(generations, best_durations, label='Best duration', color='tab:orange')

plt.legend()
plt.show()
plt.savefig(plot_path)
plt.close()