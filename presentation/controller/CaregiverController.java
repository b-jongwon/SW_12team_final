package presentation.controller;

import domain.service.CaregiverService;
import java.util.List;

public class CaregiverController {

    private final CaregiverService service = new CaregiverService();

    public List<CaregiverService.FamilySummary> getMyFamily(Long caregiverId) {
        return service.getMyFamily(caregiverId);
    }
}