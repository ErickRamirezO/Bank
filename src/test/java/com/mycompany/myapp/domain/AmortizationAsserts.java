package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class AmortizationAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmortizationAllPropertiesEquals(Amortization expected, Amortization actual) {
        assertAmortizationAutoGeneratedPropertiesEquals(expected, actual);
        assertAmortizationAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmortizationAllUpdatablePropertiesEquals(Amortization expected, Amortization actual) {
        assertAmortizationUpdatableFieldsEquals(expected, actual);
        assertAmortizationUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmortizationAutoGeneratedPropertiesEquals(Amortization expected, Amortization actual) {
        assertThat(expected)
            .as("Verify Amortization auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmortizationUpdatableFieldsEquals(Amortization expected, Amortization actual) {
        assertThat(expected)
            .as("Verify Amortization relevant properties")
            .satisfies(e -> assertThat(e.getInstallmentNumber()).as("check installmentNumber").isEqualTo(actual.getInstallmentNumber()))
            .satisfies(e -> assertThat(e.getDueDate()).as("check dueDate").isEqualTo(actual.getDueDate()))
            .satisfies(e ->
                assertThat(e.getRemainingBalance())
                    .as("check remainingBalance")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getRemainingBalance())
            )
            .satisfies(e ->
                assertThat(e.getPrincipal()).as("check principal").usingComparator(bigDecimalCompareTo).isEqualTo(actual.getPrincipal())
            )
            .satisfies(e -> assertThat(e.getPaymentDate()).as("check paymentDate").isEqualTo(actual.getPaymentDate()))
            .satisfies(e ->
                assertThat(e.getPaymentAmount())
                    .as("check paymentAmount")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getPaymentAmount())
            )
            .satisfies(e ->
                assertThat(e.getPenaltyInterest())
                    .as("check penaltyInterest")
                    .usingComparator(bigDecimalCompareTo)
                    .isEqualTo(actual.getPenaltyInterest())
            );
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmortizationUpdatableRelationshipsEquals(Amortization expected, Amortization actual) {
        assertThat(expected)
            .as("Verify Amortization relationships")
            .satisfies(e -> assertThat(e.getLoan()).as("check loan").isEqualTo(actual.getLoan()));
    }
}
