package pe.edu.pucp.teleticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.pucp.teleticket.dto.SedeFiltro;
import pe.edu.pucp.teleticket.entity.*;
import pe.edu.pucp.teleticket.repository.*;

import java.util.*;

@Controller
@RequestMapping("/obras")
public class ClienteObrasController {
    private final int obrasPaginas = 6;
    private final int funcionesPaginas = 5;
    private final int maximoDestacados = 8;



    private final List<String> formatos= Arrays.asList("media/png","media/jpeg", "image/jpeg", "image/png");

    private boolean verificarFoto(MultipartFile file){
        if(formatos.contains(file.getContentType().toLowerCase(Locale.ROOT))){
            return true;
        }
        return false;
    }

    @Autowired
    PersonaRepository personaRepository;
    @Autowired
    GeneroRepository generoRepository;
    @Autowired
    ObraRepository obraRepository;
    @Autowired
    FuncionRepository funcionRepository;
    @Autowired
    FotoObraRepository fotoObraRepository;

    @Autowired
    SedeRepository sedeRepository;
    @GetMapping({"/","","/lista"})
    public String listaObras(Model model, @RequestParam("pag") Optional<Integer> pag, @RequestParam("busqueda") Optional<String> optionalBusqueda){
        String busqueda = optionalBusqueda.isPresent()? optionalBusqueda.get().trim() : "";
        String ruta =  busqueda.isBlank()? "/obras?" : "/obras?busqueda=" +busqueda +"&";

        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        int paginas = (int) Math.ceil((float)obraRepository.contarListaObrasBusqueda(busqueda)/obrasPaginas);
        pagina = pagina>paginas? paginas : pagina;
        Pageable lista = PageRequest.of(pagina-1, obrasPaginas);
        List<Obra> listaObras = obraRepository.listarObrasBusqueda(busqueda, lista);

        model.addAttribute("listaObras",listaObras);
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("ruta", ruta);

        return "/cliente/obras/lista";
    }


    @GetMapping("/detalles/{idPath}")
    public String gestionSede(Model model, @PathVariable("idPath") String idPath, @RequestParam("pag") Optional<Integer> pag,
                              @RequestParam("ano") Optional<String> anoString, @RequestParam("mes") Optional<String> mesString,
                              @RequestParam("sede") Optional<String> sedeString){
        int id=0;
        int ano=0;
        int mes=0;
        int sede=0;
        try{
            id= Integer.parseInt(idPath);
            ano=Integer.parseInt(anoString.isPresent()? anoString.get(): "0");
            mes=Integer.parseInt(mesString.isPresent()? mesString.get(): "0");
            sede=Integer.parseInt(sedeString.isPresent()? sedeString.get(): "0");
        } catch (Exception e){
            return "redirect:/cliente/obras";
        }
        Optional<Obra> optionalObra =obraRepository.findById(id);
        if(optionalObra.isEmpty()){return "redirect:/cliente/obras";}
        int pagina = pag.isEmpty()? 1 : pag.get();
        pagina = pagina<1? 1 : pagina;
        int paginas =0;
        Pageable lista;
        String ruta ="/cliente/obras/detalles/"+id+"?";
        List<SedeFiltro> listaSedes= obraRepository.listarSedesSegunObra(id);
        List<Funcion> funcionList = new ArrayList<>();
        if((ano==0) && (sede==0)) {
            Obra obra = new Obra();
            obra.setId(id);
            paginas = (int) Math.ceil((float) funcionRepository.countAllByObra(obra) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            funcionList = funcionRepository.findAllByObraOrderByFechaAsc(obra, lista);
        } else if ((ano==0) && (sede!=0)) {
            paginas = (int) Math.ceil((float) funcionRepository.contarListaFuncionesPorSede(sede,id) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            funcionList = funcionRepository.listaFuncionesPorSede(sede,id,lista);
            ruta+= "ano="+ano+"&mes="+mes+"&sede="+sede+"&";
        } else if ((ano!=0) && (mes==0) && (sede==0)) {
            paginas = (int) Math.ceil((float) funcionRepository.contarListaFuncionesPorAno(ano,id) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            funcionList = funcionRepository.listaFuncionesPorAno(ano,id,lista);
            ruta+= "ano="+ano+"&mes="+mes+"&sede="+sede+"&";
        } else if ((ano!=0) && (mes!=0) && (sede==0)) {
            paginas = (int) Math.ceil((float) funcionRepository.contarListaFuncionesPorAnoyMes(ano,mes,id) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            funcionList = funcionRepository.listaFuncionesPorAnoyMes(ano,mes,id,lista);
            ruta+= "ano="+ano+"&mes="+mes+"&sede="+sede+"&";
        } else if ((ano!=0) && (mes!=0) && (sede!=0)) {
            paginas = (int) Math.ceil((float) funcionRepository.contarListaFuncionesPorAnoyMesySede(ano,mes,sede,id) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            funcionList = funcionRepository.listaFuncionesPorAnoyMesySede(ano,mes,sede,id,lista);
            ruta+= "ano="+ano+"&mes="+mes+"&sede="+sede+"&";
        } else if ((ano!=0) && (mes==0) && (sede!=0)) {
            paginas = (int) Math.ceil((float) funcionRepository.contarListaFuncionesPorAnoySede(ano,sede,id) / funcionesPaginas);
            paginas= paginas==0? 1: paginas;
            pagina = pagina > paginas ? paginas : pagina;
            lista = PageRequest.of(pagina-1,funcionesPaginas);
            funcionList = funcionRepository.listaFuncionesPorAnoySede(ano,sede,id,lista);
            ruta+= "ano="+ano+"&mes="+mes+"&sede="+sede+"&";
        }


        Optional<Integer> optionalMenorAno = obraRepository.obtenerPrimerAnoFuncion(id);
        Integer menorAno = optionalMenorAno.isEmpty()? 2022:optionalMenorAno.get();
        model.addAttribute("menorAno", menorAno);

        model.addAttribute("ano", ano);
        model.addAttribute("mes", mes);
        model.addAttribute("sedeId", sede);

        model.addAttribute("listaSedes",listaSedes);
        model.addAttribute("fotos", fotoObraRepository.findAllIdByIdObras(id));
        model.addAttribute("pag", pagina);
        model.addAttribute("paginas", paginas);
        model.addAttribute("obra",optionalObra.get());
        model.addAttribute("funciones", funcionList);
        model.addAttribute("ruta", ruta);
        return "/cliente/obras/detalles";
    }




    /*@PostMapping("/gestion/{idObraString}/imagenprincipal")
    public String imagenPrincipal(@PathVariable("idObraString") String idObraString, @RequestParam("fotoid") String fotoidString, RedirectAttributes redirectAttributes) {
        int idObra;
        int idFoto;
        try{
            idObra= Integer.parseInt(idObraString);
            idFoto= Integer.parseInt(fotoidString);
        }catch (Exception e){
            e.printStackTrace();
            return "redirect:/operador/obras";
        }
        Optional<Fotosobra> optionalFotosobra = fotoObraRepository.findById(idFoto);
        Optional<Obra> optionalObra = obraRepository.findById(idObra);
        if(optionalObra.isEmpty() || optionalFotosobra.isEmpty() ||
                (optionalFotosobra.get().getIdobras() != optionalObra.get().getId())){
            return "redirect:/operador/obras";
        }
        Obra obra = optionalObra.get();
        obra.setFotoprincipal(optionalFotosobra.get().getId());
        obraRepository.save(obra);
        redirectAttributes.addFlashAttribute("msg", "Se cambio la imagen principal exitosamente");
        return "redirect:/operador/obras/gestion/"+idObra;
    }
*/




}
