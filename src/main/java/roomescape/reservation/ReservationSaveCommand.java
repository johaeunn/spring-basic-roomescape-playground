package roomescape.reservation;

public record ReservationSaveCommand(
        String date,
        Long themeId,
        Long timeId,
        String memberName
) {
}
