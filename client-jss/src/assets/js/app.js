(function($) {
    "use strict";


    /*  Fixed Header*/
    $(window).on('scroll', function() {
        if ($(this).scrollTop() > 210) {
            $(".header-navbar-bottom").addClass("fixed");
        } else {
            $(".header-navbar-bottom").removeClass("fixed");
        }
    }
    );
    /*  Preloader */
    $(window).on('load', function() {
        var preLoder = $(".preloader");
        preLoder.fadeOut(1000);

    });
    /*  Mean Menu */

      $('#main_nav').meanmenu({
        meanScreenWidth: "991",
        meanMenuContainer: '.themeix-menu-selector',  
        meanMenuOpen: "<span></span><span></span><span></span>",
        meanMenuClose: "<span></span><span></span><span></span>",
       siteLogo: "<a class='navbar-mobile' href='index.html'><img src='assets/images/header-brand.png' alt='images'></a>",
      });

        /* Banner Slider */
$('.feature-slider').slick({
  dots: false,
  infinite: false,
  speed: 300,
  slidesToShow: 4, 
  slidesToScroll: 4,
  responsive: [
    {
      breakpoint: 991,
      settings: {
        slidesToShow: 3,
        slidesToScroll: 3
   } 
  },
    {
      breakpoint: 767,
      settings: {
        slidesToShow: 2,
        slidesToScroll: 2
     }
  }
  ]
});
    /*  newsticker  */
$('.newsticker-list').ticker({
  random:        false, 
  itemSpeed:     30000,  
  cursorSpeed:   50,    
  pauseOnHover:  true, 
  finishOnHover: false,  
  cursorOne:     '_',   
  cursorTwo:     '-',   
  fade:          true, 
  fadeInSpeed:   600,  
  fadeOutSpeed:  300   
});  
    /*  AOS  */
    AOS.init({
        offset: 120,
        delay: 0,
        duration: 1000,
        easing: 'ease',
        once: false,
        mirror: false,
        anchorPlacement: 'top-bottom',

    });
    // fitvideo
    /*$(".container").fitVids();*/
    /*  Scrolltop  */
    function scrolltop() {


        var wind = $(window);

        wind.on("scroll", function() {

            var scrollTop = wind.scrollTop();

            if (scrollTop >= 500) {

                $(".footer-back").addClass("show");

            } else {

                $(".footer-back").removeClass("show");
            }

        });

        $(".footer-back").on("click", function() {

            var bodyTop = $("html, body");

            bodyTop.animate({
                scrollTop: 0
            }, 500, "easeOutCubic");
        });

    }


    scrolltop();

	/*  tooltip */
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
      return new bootstrap.Tooltip(tooltipTriggerEl)
    })
    AOS.init({
      // Global settings:
      duration: 800, // values from 0 to 3000, with step 50ms
      once: true, // whether animation should happen only once - while scrolling down
    });

	/*  Footer full year */
  $('#spanYear').html(new Date().getFullYear());



}(jQuery));