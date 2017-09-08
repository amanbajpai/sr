package com.ros.smartrocket.db.store;

import android.content.ContentValues;
import android.util.SparseArray;

import com.annimon.stream.Stream;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;

import java.util.HashSet;
import java.util.Set;

public class WavesStore extends BaseStore {
    private SparseArray<ContentValues> scheduledTaskContentValuesMap;
    private SparseArray<ContentValues> hiddenTaskContentValuesMap;

    public void storeWaves(Waves waves) throws Exception {
        scheduledTaskContentValuesMap = TasksBL.getScheduledTaskHashMap(contentResolver);
        hiddenTaskContentValuesMap = TasksBL.getHiddenTaskHashMap(contentResolver);
        Wave[] tempWaves = waves.getWaves();
        Stream.of(tempWaves)
                .forEach(w -> {
                    w.setContainsDifferentRate(!isUniqueRate(w));
                    w.setRate(getMinRate(w));
                });
        TasksBL.removeNotMyTask(contentResolver);
        updateData(waves, false);
    }

    private double getMinRate(Wave wave) {
        return Stream.of(wave.getTasks())
                .map(Task::getPrice)
                .min(Double::compareTo)
                .orElse(1.0);
    }

    private boolean isUniqueRate(Wave wave) {
        Set<Double> set = new HashSet<>();
        return Stream.of(wave.getTasks())
                .map(Task::getPrice)
                .anyMatch(set::add);
    }

    public void storeMyWaves(Waves waves) throws Exception {
        scheduledTaskContentValuesMap = TasksBL.getScheduledTaskHashMap(contentResolver);
        hiddenTaskContentValuesMap = TasksBL.getHiddenTaskHashMap(contentResolver);
        SparseArray<ContentValues> validLocationTaskContentValuesMap = TasksBL.getValidLocationTaskHashMap(contentResolver);
        TasksBL.removeAllMyTask(contentResolver);
        updateData(waves, true);
        TasksBL.updateTasksByContentValues(contentResolver, validLocationTaskContentValuesMap);
    }

    private void updateData(Waves waves, boolean isMy) {
        WavesBL.saveWaveAndTaskFromServer(contentResolver, waves, isMy);
        TasksBL.updateTasksByContentValues(contentResolver, scheduledTaskContentValuesMap);
        TasksBL.updateTasksByContentValues(contentResolver, hiddenTaskContentValuesMap);
    }
}
