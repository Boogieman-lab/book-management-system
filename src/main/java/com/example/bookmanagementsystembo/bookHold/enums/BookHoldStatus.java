package com.example.bookmanagementsystembo.bookHold.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 도서 보유 상태 Enum
 * - 화면에 표시할 한글 설명(desc)을 포함
 * - 도서 상태는 대출, 연체, 예약 여부에 따라 다양하게 표시 가능
 */
@Getter
@RequiredArgsConstructor
public enum BookHoldStatus {

    /** 대출 가능: 현재 대출자가 없고 바로 대출 가능 */
    AVAILABLE("대출가능"),

    /** 예약 중: 대출 가능하지만 예약자가 존재하여 예약 대기 필요 */
    RESERVED("예약중"),

    /** 대출 중: 현재 대출자가 있으며 아직 반납하지 않음 */
    BORROWED("대출중"),

    /** 연체 중: 대출자가 있으나 반납 기한을 지남 */
    OVERDUE("연체중"),

    /** 분실됨: 도서가 분실되었거나 관리상 존재하지 않음 */
    LOST("분실"),

    /** 폐기됨: 더 이상 도서로 사용 불가 */
    DISCARDED("폐기");

    /** 화면에 표시할 한글 설명 */
    private final String desc;
}
