package com.ros.smartrocket.db.store;

import android.content.ContentValues;

import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.AskIf;
import com.ros.smartrocket.db.entity.Category;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Questions;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.TaskLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionStore extends BaseStore {
    private Task task;
    private Gson gson = new Gson();

    public QuestionStore(Task task) {
        this.task = task;
    }

    public void storeQuestions(Questions questions) {
        if (questions != null && questions.getQuestions() != null && questions.getQuestions().length > 0) {
            QuestionsBL.removeQuestionsFromDB(task);
            questions.setQuestions(QuestionsBL.sortQuestionsByOrderId(questions.getQuestions()));
            int i = 1;
            for (Question question : questions.getQuestions()) {
                if (i != 1 && question.getShowBackButton())
                    question.setPreviousQuestionOrderId(i - 1);
                i = insertQuestion(i, question);
            }
            if (questions.getMissionSize() != null)
                WavesBL.updateWave(task.getWaveId(), questions.getMissionSize());
        }
    }

    private int insertQuestion(int i, Question question) {
        List<ContentValues> answersValues = new ArrayList<>();
        List<ContentValues> questionValues = new ArrayList<>();
        question = prepareQuestion(i, question);
        questionValues.add(question.toContentValues());
        deleteAnswers(question);
        if (question.getChildQuestions() != null && question.getChildQuestions().length > 0) {
            List<Product> productList = makeProductList(question.getCategoriesArray());
            int j = 1;
            for (Question childQuestion : question.getChildQuestions()) {
                deleteAnswers(childQuestion);
                childQuestion = prepareQuestion(j, childQuestion);
                questionValues.add(childQuestion.toContentValues());
                answersValues.addAll(getAnswerValues(childQuestion, productList));
                j++;
            }
        }
        answersValues.addAll(getAnswerValues(question, null));

        if (!answersValues.isEmpty()) {
            ContentValues[] valuesArray = new ContentValues[answersValues.size()];
            valuesArray = answersValues.toArray(valuesArray);
            contentResolver.bulkInsert(AnswerDbSchema.CONTENT_URI, valuesArray);
        }
        if (!questionValues.isEmpty()) {
            ContentValues[] valuesArray = new ContentValues[questionValues.size()];
            valuesArray = questionValues.toArray(valuesArray);
            contentResolver.bulkInsert(QuestionDbSchema.CONTENT_URI, valuesArray);
        }
        i++;
        return i;
    }

    private void deleteAnswers(Question question) {
        contentResolver.delete(AnswerDbSchema.CONTENT_URI,
                AnswerDbSchema.Columns.QUESTION_ID + "=? and " + AnswerDbSchema.Columns.TASK_ID + "=?",
                new String[]{String.valueOf(question.getId()), String.valueOf(task.getId())});
    }

    private List<Product> makeProductList(Category[] categoriesArray) {
        List<Product> productList = new ArrayList<>();
        if (categoriesArray != null)
            Stream.of(categoriesArray)
                    .map(Category::getProducts)
                    .filter(p -> p != null)
                    .forEach(p -> Collections.addAll(productList, p));
        return productList;
    }


    private List<ContentValues> getAnswerValues(Question question, List<Product> productList) {
        List<ContentValues> answersValues = new ArrayList<>();
        if (productList != null) {
            Stream.of(productList)
                    .filter(product -> question.getProductId() == null || product.getId().equals(question.getProductId()))
                    .forEach(product -> {
                        if (question.getAnswers() != null && question.getAnswers().length > 0)
                            Stream.of(question.getAnswers())
                                    .forEach(a -> answersValues.add(prepareAnswer(question, a, product.getId()).toContentValues()));
                        else
                            answersValues.add(prepareAnswer(question, new Answer(), product.getId()).toContentValues());

                    });
        } else {
            if (question.getAnswers() != null && question.getAnswers().length > 0)
                for (Answer answer : question.getAnswers()) {
                    answersValues.add(prepareAnswer(question, answer, null).toContentValues());
                }
            else
                answersValues.add(prepareAnswer(question, new Answer(), null).toContentValues());
        }
        return answersValues;
    }

    private Answer prepareAnswer(Question question, Answer answer, Integer productId) {
        if (question.getType() == Question.QuestionType.MAIN_SUB_QUESTION.getTypeId() && question.isRedo()) {
            answer.setChecked(false);
        }
        answer.setRandomId();
        answer.setProductId(productId);
        answer.setQuestionId(question.getId());
        answer.setTaskId(task.getId());
        answer.setMissionId(task.getMissionId());
        return answer;
    }


    protected Question prepareQuestion(int i, Question question) {
        AskIf[] askIfArray = question.getAskIfArray();
        Category[] categoriesArray = question.getCategoriesArray();
        TaskLocation taskLocation = question.getTaskLocationObject();

        question.setTaskId(task.getId());
        question.setMissionId(task.getMissionId());
        if (askIfArray != null) question.setAskIf(gson.toJson(askIfArray));
        if (categoriesArray != null) question.setCategories(gson.toJson(categoriesArray));
        if (taskLocation != null) {
            taskLocation.setCustomFields(gson.toJson(taskLocation.getCustomFieldsMap()));
            question.setTaskLocation(gson.toJson(taskLocation));
        }
        return question;
    }

}
