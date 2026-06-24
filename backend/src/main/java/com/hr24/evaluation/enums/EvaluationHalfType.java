package com.hr24.evaluation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EvaluationHalfType {

    H1("상반기"),
    H2("하반기");

    private final String label;
}