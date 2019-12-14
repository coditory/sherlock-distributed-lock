package com.coditory.sherlock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.coditory.sherlock.Preconditions.expect;
import static com.coditory.sherlock.Preconditions.expectEqual;
import static com.coditory.sherlock.Preconditions.expectNonEmpty;

class ChangeSetMethodExtractor {
  static <R> List<MigrationChangeSet<R>> extractChangeSets(
    Object object, Class<R> expectedReturnType) {
    List<MigrationChangeSet<R>> result = new ArrayList<>();
    Map<ChangeSet, Method> changeSetMethods = extractAnnotatedChangeSetMethods(
      object, expectedReturnType);
    expectNonEmpty(
      changeSetMethods, "Expected at least one changeset method annotated with @ChangeSet");
    List<ChangeSet> annotations = new ArrayList<>(changeSetMethods.keySet());
    annotations.sort(Comparator.comparingInt(ChangeSet::order));
    ChangeSet lastChangeSet = null;
    for (ChangeSet changeSet : annotations) {
      if (lastChangeSet != null) {
        expect(
          lastChangeSet.order() < changeSet.order(),
          "Expected unique change set order values. Duplicated: " + changeSet.order());
      }
      Supplier<R> action = invokeMethod(
        changeSetMethods.get(changeSet), object, expectedReturnType);
      result.add(new MigrationChangeSet<>(changeSet.id(), action));
      lastChangeSet = changeSet;
    }
    return result;
  }

  private static Map<ChangeSet, Method> extractAnnotatedChangeSetMethods(
    Object object, Class<?> expectedReturnType) {
    Map<ChangeSet, Method> changeSetMethods = new HashMap<>();
    Method[] methods = object.getClass().getMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(ChangeSet.class)) {
        ChangeSet changeSet = method.getAnnotation(ChangeSet.class);
        validateAnnotatedChangeSet(changeSet, method, expectedReturnType);
        changeSetMethods.put(changeSet, method);
      }
    }
    return changeSetMethods;
  }

  private static void validateAnnotatedChangeSet(
    ChangeSet changeSet, Method method, Class<?> expectedReturnType) {
    expectEqual(
      method.getParameterCount(), 0,
      "Expected no declared parameters for method " + method.getName());
    expectEqual(
      method.getReturnType(), expectedReturnType,
      "Expected method to declare void as return type. Method:" + method.getName()
        + " return type: " + method.getReturnType());
    expect(
      changeSet.order() >= 0,
      "Expected changeset order >= 0. Method:" + method.getName());
    expectNonEmpty(
      changeSet.id(),
      "Expected non-empty changeset id. Method:" + method.getName());
  }

  @SuppressWarnings("unchecked")
  static private <R> Supplier<R> invokeMethod(
    Method method, Object object, Class<R> expectedReturnType) {
    return () -> {
      try {
        return (R) method.invoke(object);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException(
          "Could not invoke changeset method: " + method.getName(), e);
      }
    };
  }
}
