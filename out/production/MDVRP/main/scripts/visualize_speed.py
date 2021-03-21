import matplotlib.pyplot as plt
import numpy as np

training_data_path = '../resources/training_data.txt'
plot_path = '../resources/training_data_speed.jpg'

generations, time_stamps, best_durations, average_durations = np.loadtxt(training_data_path, unpack=True)

plt.title('Training data')
plt.xlabel('time / (s)')
plt.ylabel('$\Delta$duration / $\Delta$t')

dt = (time_stamps[:-1] + time_stamps[1:]) / 2
plt.plot(dt, np.diff(average_durations / time_stamps), label='Average duration / t', color='k')
# plt.plot(dt, np.diff(best_durations / time_stamps), label='Best duration / t', color='deepskyblue')

plt.plot(time_stamps, average_durations, label='Average duration', color='k')
# plt.plot(time_stamps, best_durations, label='Best duration', color='deepskyblue')

plt.legend()
plt.savefig(plot_path)
plt.close()
