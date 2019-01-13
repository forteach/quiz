package com.forteach.quiz.questionLibrary.reflect;

import com.forteach.quiz.exceptions.CustomException;
import com.forteach.quiz.questionLibrary.domain.question.ChoiceQst;
import com.forteach.quiz.questionLibrary.domain.question.ChoiceQstOption;
import com.forteach.quiz.questionLibrary.domain.question.Design;
import com.forteach.quiz.questionLibrary.domain.question.TrueOrFalse;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static com.forteach.quiz.util.StringUtil.getRandomUUID;
import static com.forteach.quiz.util.StringUtil.isEmpty;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  15:10
 */
@Component
public class QuestionReflect {

    /**
     * 为题目对象设置id,以及 ExamType等属性
     *
     * @param object
     */
    public void buildAttribute(final Object object) {

        Class clazz = object.getClass();

        try {
            //设置属性
            setupAttribute(object, clazz);
        } catch (NoSuchMethodException | CustomException | InvocationTargetException | IllegalAccessException e) {
            throw new CustomException("反射赋值 出错" + e);
        }

    }

    /**
     * 设置属性
     *
     * @param object
     * @param clazz
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setupAttribute(final Object object, final Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //设置question id
        setupId(object, clazz);
        //设置ExamType 及选择题子参数
        setupQuestionType(object, clazz);
    }

    /**
     * 设置id
     * 根据id是否为空
     */
    private void setupId(final Object object, final Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method getId = getIdMethod(clazz);
        Method setId = setIdMethod(clazz);
        String id = (String) getId.invoke(object);

        if (isEmpty(id)) {
            setId.invoke(object, getRandomUUID());
        }
    }

    /**
     * 设置ExamType 及选择题子参数
     *
     * @param object
     * @param clazz
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setupQuestionType(final Object object, final Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method setExamType = setExamTypeMethod(clazz);

        if (TrueOrFalse.class.isAssignableFrom(clazz)) {
            //设置判断题参数
            setExamType.invoke(object, "trueOrFalse");
        } else if (ChoiceQst.class.isAssignableFrom(clazz)) {

            //设置选择题参数 及 选择题子项参数
            Method getChoiceType = getChoiceTypeMethod(clazz);
            String choiceType = (String) getChoiceType.invoke(object);
            setExamType.invoke(object, choiceType);
            setupOptChildrenId(object, clazz);

        } else if (Design.class.isAssignableFrom(clazz)) {
            //设置主观题参数
            setExamType.invoke(object, "design");
        } else {
            throw new CustomException("获取题目type 出错");
        }
    }

    /**
     * 设置选择题选项id
     *
     * @param object
     * @param clazz
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setupOptChildrenId(final Object object, final Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method getOptChildren = getOptChildrenMethod(clazz);
        Method setOptChildren = setOptChildrenMethod(clazz);

        List<ChoiceQstOption> id = (List<ChoiceQstOption>) getOptChildren.invoke(object);

        setOptChildren.invoke(object, id.stream()
                .peek(choiceQstOption -> {
                    if (isEmpty(choiceQstOption.getId())) {
                        choiceQstOption.setId(getRandomUUID());
                    }
                })
                .collect(Collectors.toList()));
    }

    /**
     * 反射设置id
     *
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     */
    private Method setIdMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getSuperclass().getDeclaredMethod("setId", String.class);
    }

    /**
     * 反射获取id
     *
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     */
    private Method getIdMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getSuperclass().getDeclaredMethod("getId");
    }

    /**
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     */
    private Method setExamTypeMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getSuperclass().getDeclaredMethod("setExamType", String.class);
    }

    /**
     * 反射获取选择题类型
     *
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     */
    private Method getChoiceTypeMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getDeclaredMethod("getChoiceType");
    }

    /**
     * 反射获取选择题选项
     *
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     */
    private Method getOptChildrenMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getDeclaredMethod("getOptChildren");
    }

    /**
     * 反射设置选择题选项
     *
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     */
    private Method setOptChildrenMethod(Class clazz) throws NoSuchMethodException {
        return clazz.getDeclaredMethod("setOptChildren", List.class);
    }

}
