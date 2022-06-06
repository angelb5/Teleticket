$(document).ready(function(e){
    var rol = $('.search-panel span#rol').text();
    if(rol != "admin" && rol !="operador"){
        rol = "/";
    }else{
        rol = "/"+rol+"/";
    }
    var buscaren = $('.search-panel span#buscaren').text();
    $('#busquedaFrm').attr('action', rol+buscaren)

    $('.search-panel .dropdown-menu').find('a').click(function(e) {
        e.preventDefault();
        var param = $(this).attr("href").replace("#","");
        var concept = $(this).text();
        $('.search-panel span#search_concept').text(concept);
        $('#busquedaFrm').attr('action', rol+param)
    });

});