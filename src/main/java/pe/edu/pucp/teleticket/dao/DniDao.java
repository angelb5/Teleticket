package pe.edu.pucp.teleticket.dao;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pe.edu.pucp.teleticket.dto.DniDto;

import java.util.List;

@Component
public class DniDao {
    public boolean verificarDni(String dni) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("yOgeqH51FG63iLlp5CYyMyi3D0jaSEF9hDj3HS7IbCg20YJh1m65Fi4c8IyW");
        HttpEntity<DniDto> entity = new HttpEntity<>(new DniDto(), headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.peruapis.com/v1/dni?document=" + dni;

        ResponseEntity<DniDto> responseMap = restTemplate.exchange(url, HttpMethod.GET, entity, DniDto.class);

        DniDto dniDto = responseMap.getBody();
        if (dniDto==null || dniDto.getSuccess()==null || dniDto.getSuccess().equals("false") || dniDto.getData()==null ||
                dniDto.getData().getFullname()==null || dniDto.getData().getFullname().trim().equals("")){
            return  false;
        }
        return true;
    }
}
