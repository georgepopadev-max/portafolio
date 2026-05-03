/**
 * Portfolio - George Popa
 * Animations & Interactions
 */

(function() {
  'use strict';

  // ========================================
  // Mobile Navigation
  // ========================================
  const hamburger = document.querySelector('.nav__hamburger');
  const mobileMenu = document.querySelector('.mobile-menu');
  const mobileLinks = document.querySelectorAll('.mobile-menu__link');

  function toggleMobileMenu() {
    hamburger.classList.toggle('active');
    mobileMenu.classList.toggle('active');
    document.body.style.overflow = mobileMenu.classList.contains('active') ? 'hidden' : '';
  }

  function closeMobileMenu() {
    hamburger.classList.remove('active');
    mobileMenu.classList.remove('active');
    document.body.style.overflow = '';
  }

  if (hamburger) {
    hamburger.addEventListener('click', toggleMobileMenu);
  }

  mobileLinks.forEach(link => {
    link.addEventListener('click', closeMobileMenu);
  });

  // Close mobile menu on escape key
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && mobileMenu.classList.contains('active')) {
      closeMobileMenu();
    }
  });

  // ========================================
  // Typing Effect
  // ========================================
  class TypeWriter {
    constructor(element, words, wait = 3000) {
      this.element = element;
      this.words = words;
      this.wait = parseInt(wait, 10);
      this.wordIndex = 0;
      this.txt = '';
      this.isDeleting = false;
      this.init();
    }

    init() {
      this.type();
    }

    type() {
      const current = this.wordIndex % this.words.length;
      const fullTxt = this.words[current];

      if (this.isDeleting) {
        this.txt = fullTxt.substring(0, this.txt.length - 1);
      } else {
        this.txt = fullTxt.substring(0, this.txt.length + 1);
      }

      this.element.innerHTML = this.txt + '<span class="typing-cursor"></span>';

      let typeSpeed = 100;

      if (this.isDeleting) {
        typeSpeed /= 2;
      }

      if (!this.isDeleting && this.txt === fullTxt) {
        typeSpeed = this.wait;
        this.isDeleting = true;
      } else if (this.isDeleting && this.txt === '') {
        this.isDeleting = false;
        this.wordIndex++;
        typeSpeed = 500;
      }

      setTimeout(() => this.type(), typeSpeed);
    }
  }

  // Initialize typing effect
  const typingElement = document.querySelector('.hero__title');
  if (typingElement) {
    const words = typingElement.dataset.words 
      ? typingElement.dataset.words.split(',') 
      : ['Full Stack Developer', 'Java Enthusiast', 'Energy Sector Expert'];
    
    // Clear initial text
    typingElement.textContent = '';
    new TypeWriter(typingElement, words, 3000);
  }

  // ========================================
  // Scroll Reveal with Intersection Observer
  // ========================================
  const revealElements = document.querySelectorAll('.reveal');

  const revealObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        // Optional: unobserve after reveal
        // revealObserver.unobserve(entry.target);
      }
    });
  }, {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  });

  revealElements.forEach(el => {
    revealObserver.observe(el);
  });

  // ========================================
  // Smooth Scroll Navigation
  // ========================================
  const navLinks = document.querySelectorAll('a[href^="#"]');

  navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
      const href = link.getAttribute('href');
      if (href === '#') return;

      e.preventDefault();
      const target = document.querySelector(href);
      
      if (target) {
        const headerOffset = 80;
        const elementPosition = target.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.pageYOffset - headerOffset;

        window.scrollTo({
          top: offsetPosition,
          behavior: 'smooth'
        });
      }
    });
  });

  // ========================================
  // Active Navigation Link on Scroll
  // ========================================
  const sections = document.querySelectorAll('section[id]');
  const navLinksList = document.querySelectorAll('.nav__link');

  function updateActiveNav() {
    const scrollY = window.scrollY + window.innerHeight / 3;

    let currentSection = null;
    sections.forEach(section => {
      const sectionTop = section.offsetTop - 100;
      if (scrollY >= sectionTop) {
        currentSection = section.getAttribute('id');
      }
    });

    if (currentSection) {
      navLinksList.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === `#${currentSection}`) {
          link.classList.add('active');
        }
      });
    }
  }

  window.addEventListener('scroll', updateActiveNav);

  // ========================================
  // Navigation Background on Scroll
  // ========================================
  const nav = document.querySelector('.nav');

  function updateNavBg() {
    if (window.scrollY > 50) {
      nav.style.background = 'rgba(10, 10, 15, 0.95)';
    } else {
      nav.style.background = 'rgba(10, 10, 15, 0.85)';
    }
  }

  window.addEventListener('scroll', updateNavBg);

  // ========================================
  // Page Transition Loader (optional enhancement)
  // ========================================
  window.addEventListener('load', () => {
    document.body.classList.add('loaded');
    
    // Trigger initial animations
    const heroElements = document.querySelectorAll('.hero > *');
    heroElements.forEach((el, index) => {
      el.style.animationDelay = `${index * 0.1}s`;
    });
  });

  // ========================================
  // Keyboard Navigation Support
  // ========================================
  document.addEventListener('keydown', (e) => {
    // Tab navigation visibility enhancement
    if (e.key === 'Tab') {
      document.body.classList.add('keyboard-nav');
    }
  });

  document.addEventListener('mousedown', () => {
    document.body.classList.remove('keyboard-nav');
  });

  // ========================================
  // Performance: Throttle Scroll Events
  // ========================================
  let ticking = false;

  function onScroll() {
    if (!ticking) {
      window.requestAnimationFrame(() => {
        updateNavBg();
        updateActiveNav();
        ticking = false;
      });
      ticking = true;
    }
  }

  window.addEventListener('scroll', onScroll, { passive: true });

})();