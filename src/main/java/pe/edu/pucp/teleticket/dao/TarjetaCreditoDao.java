package pe.edu.pucp.teleticket.dao;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pe.edu.pucp.teleticket.dto.Informacion;
import pe.edu.pucp.teleticket.dto.TarjeCreditoRespuesta;
import pe.edu.pucp.teleticket.dto.TarjetaCompra;
import pe.edu.pucp.teleticket.dto.TarjetaCredito;

import java.math.BigDecimal;
import java.util.HashMap;

@Getter
@Setter
@Component
public class TarjetaCreditoDao {

    private final String usuario= "teleticket";
    private final String password= "4675bedyv86g8nyiv21s07";
    private final String url= "http://apitarjeta.japaneast.cloudapp.azure.com";

    public boolean validarTarjeta(TarjetaCredito tarjetaCredito){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TarjetaCredito> httpEntity = new HttpEntity<>(tarjetaCredito, headers);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(usuario,password)
                .build();
        ResponseEntity<TarjeCreditoRespuesta> response = restTemplate.postForEntity(
                url+"/tarjeta/verificar", httpEntity,TarjeCreditoRespuesta.class);
        TarjeCreditoRespuesta respuestaRecibida = response.getBody();
        if(respuestaRecibida.getEstado().equalsIgnoreCase("verificado")){
            return true;
        }
        return false;
    }

    public HashMap<String,String> comprar(TarjetaCompra compra){
        HashMap<String,String> resultado = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TarjetaCompra> httpEntity = new HttpEntity<>(compra, headers);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(usuario,password)
                .build();
        ResponseEntity<TarjeCreditoRespuesta> response = restTemplate.postForEntity(
                url+"/tarjeta/compra", httpEntity,TarjeCreditoRespuesta.class);
        TarjeCreditoRespuesta respuestaRecibida = response.getBody();
        if(respuestaRecibida.getEstado().equalsIgnoreCase("exito")){
            resultado.put("estado", "exito");
        } else{
            resultado.put("estado", "error");
            resultado.put("msg", respuestaRecibida.getMsg());
        }
        return resultado;
    }

    public HashMap<String,String> cancelar(Integer id){
        HashMap<String,String> resultado = new HashMap<>();
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(usuario,password)
                .build();
        ResponseEntity<TarjeCreditoRespuesta> response = restTemplate.getForEntity(
                url+"/tarjeta/cancelar/"+id,TarjeCreditoRespuesta.class);
        TarjeCreditoRespuesta respuestaRecibida = response.getBody();
        if(respuestaRecibida.getEstado().equalsIgnoreCase("exito")){
            resultado.put("estado", "exito");
        } else{
            resultado.put("estado", "error");
            resultado.put("msg", respuestaRecibida.getMsg());
        }
        return resultado;
    }

}
