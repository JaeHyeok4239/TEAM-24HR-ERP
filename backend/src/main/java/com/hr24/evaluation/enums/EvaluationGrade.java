package com.hr24.evaluation.enums;

public enum EvaluationGrade {

    S, A, B, C, D;

    public static EvaluationGrade from(int totalScore) {
        if (totalScore >= 43) return S;
        if (totalScore >= 38) return A;
        if (totalScore >= 33) return B;
        if (totalScore >= 28) return C;
        return D;
    }
}
