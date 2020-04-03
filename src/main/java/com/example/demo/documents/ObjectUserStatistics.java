package com.example.demo.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectUserStatistics {
    private int addNewCar__________________ = 0;
    private int updateCar__________________ = 0;
    private int deleteCar__________________ = 0;
    private int ownerGetCars_______________ = 0;
    private int ownerGetCarBySerialNumber__ = 0;
    private int ownerGetBookedPeriodByCarId = 0;
    private int addNewCommentByCarId_______ = 0;
    private int paymentForReservation______ = 0;
    private int makeReservation____________ = 0;
    private int reservationCancellation____ = 0;
    private int unlockCarForBookingCarId___ = 0;
    private int lockCarForBookingCarId_____ = 0;
    private int registrationUser___________ = 0;
    private int updateUser_________________ = 0;
    private int deleteUser_________________ = 0;
    private int getBookedList______________ = 0;
    private int getInvoice_________________ = 0;
    private int authUser___________________ = 0;
    private int getHistory_________________ = 0;
}
