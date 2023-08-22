package app.platform.equipment.controller;

import app.platform.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @GetMapping("/get")
    public R<String> get(){
        return R.ok("water meter");
    }

}
