package ru.practicum.shareit.booking;

public enum State {
    ALL, //все бронирования
    CURRENT, //текущие бронирования
    PAST, //завершенные бронирования
    FUTURE, //будущие бронирования
    WAITING, //бронирования, ожидающие подтверждения
    REJECTED, //отклоненные бронирования
    UNSUPPORTED_STATUS; //передан неподдерживаемый статус

    public static State fromValue(String value) {
       for (State state : values()) {
           if (state.toString().equals(value)) {
               return state;
           }
       }
       return UNSUPPORTED_STATUS;
    }
}
