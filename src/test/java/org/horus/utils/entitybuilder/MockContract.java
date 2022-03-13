package org.horus.utils.entitybuilder;

class MockContract {

    private final String code;

    MockContract() {
        code = null;
    }

    MockContract(String code) {
        this.code = code;
    }

    String getCode() {
        return code;
    }

}
