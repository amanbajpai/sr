package com.ros.smartrocket.utils.helpers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.SparseArray;

import com.ros.smartrocket.App;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;

public class WavesStoreHelper {
    private ContentResolver contentResolver;
    private SparseArray<ContentValues> scheduledTaskContentValuesMap;
    private SparseArray<ContentValues> hiddenTaskContentValuesMap;

    public WavesStoreHelper() {
        contentResolver = App.getInstance().getContentResolver();
    }

    public void storeWaves(Waves waves) throws Exception {
        scheduledTaskContentValuesMap = TasksBL.getScheduledTaskHashMap(contentResolver);
        hiddenTaskContentValuesMap = TasksBL.getHiddenTaskHashMap(contentResolver);
        Wave[] tempWaves = waves.getWaves();
        for (Wave tempWave : tempWaves) {
            Task[] tempTasks = tempWave.getTasks();
            for (int j = 1; j < tempTasks.length; j++) {
                if (tempTasks[j].getPrice() != tempTasks[j - 1].getPrice()) {
                    tempWave.setContainsDifferentRate(true);
                    break;
                }
            }
        }

        for (Wave tempWave : tempWaves) {
            Task[] tempTasks = tempWave.getTasks();
            double min = tempTasks[0].getPrice();
            for (Task tempTask : tempTasks) {
                if (tempTask.getPrice() < min) {
                    min = tempTask.getPrice();
                }
            }
            tempWave.setRate(min);
        }

        TasksBL.removeNotMyTask(contentResolver);
        WavesBL.saveWaveAndTaskFromServer(contentResolver, waves, false);

        TasksBL.updateTasksByContentValues(contentResolver, scheduledTaskContentValuesMap);
        TasksBL.updateTasksByContentValues(contentResolver, hiddenTaskContentValuesMap);
    }

    public void stroreMyWaves(Waves waves) throws Exception {
        scheduledTaskContentValuesMap = TasksBL.getScheduledTaskHashMap(contentResolver);
        hiddenTaskContentValuesMap = TasksBL.getHiddenTaskHashMap(contentResolver);
        SparseArray<ContentValues> validLocationTaskContentValuesMap = TasksBL.getValidLocationTaskHashMap(contentResolver);

        TasksBL.removeAllMyTask(contentResolver);
        WavesBL.saveWaveAndTaskFromServer(contentResolver, waves, true);

        //Update task status id
        TasksBL.updateTasksByContentValues(contentResolver, scheduledTaskContentValuesMap);
        TasksBL.updateTasksByContentValues(contentResolver, hiddenTaskContentValuesMap);
        TasksBL.updateTasksByContentValues(contentResolver, validLocationTaskContentValuesMap);
    }
}
