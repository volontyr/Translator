/**
 * Created by santos on 4/18/16.
 */
$(document).ready(function() {
    //var xmlhttp;
    //if (window.XMLHttpRequest) {
    //    xmlhttp = new XMLHttpRequest();
    //}
    //else {
    //    xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    //}
    //xmlhttp.open("GET","resources/tree.xml",false);
    //xmlhttp.send();
    //var xml = xmlhttp.responseXML;
    //alert(xml);
    $.ajax({
        type: "GET",
        url: "resources/tree.xml",
        dataType: "xml",
        success: function (xml) {
            $('#tree').find('ul').remove();
            $('<ul></ul>').addClass('tree non-terminal').addClass('active').appendTo('#tree');
            $('.tree').html('<span>Tree</span>');
            $(xml).children('non-terminal').each(function () {
                parser($(this));
                $('.active').removeClass('active');
                $('.catalog').addClass('active');
            });
            $('.tree').children('span:first').off();

            $('.active').removeClass('active');
            $("ul").addClass('list-group');
            $("li").addClass('list-group-item');
        }
    });

    $(function() {
        $(window).scroll(function() {
            var top = $(this).scrollTop();
            $('#buttons').css('margin-top', top + 10);
        });
    });
});

function parser(dir) {
    $('<li></li>').addClass('tmp').appendTo('.active');
    $('.tmp').html(
        '<span>'+$(dir).attr('name')+'</span>'+'<ul></ul>'
    );
    $('.active').removeClass('active');
    $('.tmp ul').addClass('active');
    $('.tmp').addClass('non-terminal').removeClass('tmp');

    $("span").off().on("click", function() {
        $(this).parent('.non-terminal').find("ul").children("li").toggle();
        return false;
    });

    $(dir).children('terminal').each(function() {
        $('<li id='+$(this).text()+'></li><br/>').html($(this).attr('name'))
            .addClass('terminal').appendTo('.active');
    });
    if ($(dir).children('non-terminal').length > 0) {
        $(dir).children('non-terminal').each(function() {
            parser($(this));
        });
    }
    $('.active').removeClass('active').parents("ul:first").addClass('tmp');
    $('.tmp').removeClass('tmp').addClass('active');
}

