## Utility Project
    mvn clean install

## Ajout des users
    User user = new User( LocalDateTime.now(), LocalDateTime.now(), "admin@admin.sn", "a", PasswordUtils.hashPassword("a"), true, Role.ADMIN);
    userService.add(user);
    User client = new User(LocalDateTime.now(), LocalDateTime.now(), "client@admin.sn", "c", PasswordUtils.hashPassword("c"), true, Role.CLIENT);
    userService.add(client);
    User boutiquier = new User(LocalDateTime.now(), LocalDateTime.now(), "boutiquier@admin.sn", "b", PasswordUtils.hashPassword("b"), true, Role.BOUTIQUIER);
    userService.add(boutiquier);

## Couleur du projet
    colors: {
      first: '#003049',
      two: '#6a994e',
      third: '#a7c957',
      info: '#F2E8CF',
      warning: '#FFB723',
      error: '#BC4749',
      gray: {
        100: '#F9FAFC',
        200: '#F3F4F6',
        300: '#E5E7EB',
        400: '#D1D3D8',
        500: '#A0A3A8',
        600: '#71717A',
        700: '#525356',
        800: '#3F4144',
        900: '#2D3033',
      },
    }
