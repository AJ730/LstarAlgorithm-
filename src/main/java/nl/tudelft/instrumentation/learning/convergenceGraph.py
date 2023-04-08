import matplotlib.pyplot as plt
import numpy as np

x1 = [0, 28, 31, 32, 34, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35]
x2 = [0, 26, 27, 30, 34, 36, 37, 39, 46, 48, 50, 51, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55]
x4 = [0, 30, 40, 44, 49, 60, 63, 70, 71, 76, 78, 80, 81, 82, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]
x7 = [0, 34, 37, 45, 57, 58, 59, 62, 63, 64, 73, 76, 79, 86, 87, 88, 90, 92, 94, 95, 96, 102, 103, 104, 105, 106]
xpin = [0, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6]
y = range(0, 26, 1)

if __name__ == '__main__':
    plt.plot(y, x1, label="Problem 1")
    plt.plot(y, x2, label="Problem 2")
    plt.plot(y, x4, label="Problem 4")
    plt.plot(y, x7, label="Problem 7")
    plt.plot(y, xpin, label="Problem Pin")
    plt.ylabel("Number of states")
    plt.xlabel("Iterations")
    plt.title("States vs Iterations")
    plt.xlim([0, 25])
    plt.ylim([0, 110])
    plt.yticks(np.arange(0, 111, 10))
    plt.xticks(np.arange(0, 26, 2))
    plt.legend(bbox_to_anchor=(1.0, 0.5), loc='center left')
    plt.tight_layout()
    plt.show()
