package com.umanbeing.umg.configs;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.umanbeing.umg.models.Location;
import com.umanbeing.umg.repos.LocationRepo;

@Configuration
@Profile({"dev", "prod"})
public class DevSeedConfig {

  @Bean
  CommandLineRunner seedLocations(LocationRepo locationRepo) {
    return args -> {
      if (locationRepo.count() > 0) {
        return;
      }

      locationRepo.saveAll(List.of(
        new Location("", "https://uman-beings.github.io/UMG-Pictures/crazy-balance-v0-kzeho9rnepre1.jpeg",new BigDecimal("2282"), new BigDecimal("1340")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/deer1.jpeg", new BigDecimal("2705"), new BigDecimal("833")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3651.jpg", new BigDecimal("1770"), new BigDecimal("1480")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3652.jpg", new BigDecimal("1679"), new BigDecimal("1173")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3655.jpg", new BigDecimal("1209"), new BigDecimal("1344")),

        new Location("", "https://uman-beings.github.io/UMG-Pictures/56jes45xzfdg1.jpeg",new BigDecimal("2813"), new BigDecimal("1143")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/by35nk00kzcg1.jpeg", new BigDecimal("2718"), new BigDecimal("1065")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3659.jpg", new BigDecimal("1259"), new BigDecimal("1262")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3660.jpeg", new BigDecimal("1545"), new BigDecimal("1210")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3662.jpg", new BigDecimal("2476"), new BigDecimal("1350")),

        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3663.jpeg",new BigDecimal("2785"), new BigDecimal("1665")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3665.jpg", new BigDecimal("2774"), new BigDecimal("1756")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3670.jpg", new BigDecimal("3237"), new BigDecimal("1684")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3673.jpg", new BigDecimal("3228"), new BigDecimal("1694")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3675.jpg", new BigDecimal("2728"), new BigDecimal("1928")),

        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3712.jpg",new BigDecimal("3438"), new BigDecimal("815")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3676.jpeg", new BigDecimal("2688"), new BigDecimal("1992")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3678.jpg", new BigDecimal("2301"), new BigDecimal("2303")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3680.jpeg", new BigDecimal("2068"), new BigDecimal("2109")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3689.jpg", new BigDecimal("1966"), new BigDecimal("2150")),

        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3696.jpg",new BigDecimal("2044"), new BigDecimal("1728")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3697.jpg", new BigDecimal("2206"), new BigDecimal("1668")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3698.jpg", new BigDecimal("2387"), new BigDecimal("1787")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3702.jpg", new BigDecimal("2722"), new BigDecimal("1070")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3706.jpeg", new BigDecimal("2955"), new BigDecimal("923")),
        
        new Location("", "https://uman-beings.github.io/UMG-Pictures/lol-v0-lf22qalq7ryf1.jpeg", new BigDecimal("811"), new BigDecimal("963")),

        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3722.jpg", new BigDecimal("2228"), new BigDecimal("607")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3723.jpg", new BigDecimal("2319"), new BigDecimal("738")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3724.jpg", new BigDecimal("2394"), new BigDecimal("692")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3727.jpg", new BigDecimal("2467"), new BigDecimal("694")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3732.jpg", new BigDecimal("2246"), new BigDecimal("801")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3733.jpg", new BigDecimal("2245"), new BigDecimal("784")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3735.jpg", new BigDecimal("2009"), new BigDecimal("997")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3736.jpg", new BigDecimal("2015"), new BigDecimal("1172")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3740.jpg", new BigDecimal("2801"), new BigDecimal("1751")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3744.jpg", new BigDecimal("2904"), new BigDecimal("1741")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3745.jpg", new BigDecimal("2906"), new BigDecimal("1676")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_3748.jpg", new BigDecimal("2882"), new BigDecimal("1001")),

        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG20260305154756.jpg", new BigDecimal("2250"), new BigDecimal("1642")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG20260305161029.jpg", new BigDecimal("2815"), new BigDecimal("1465")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG20260305161918.jpg", new BigDecimal("2614"), new BigDecimal("2237")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG20260305162045.jpg", new BigDecimal("2614"), new BigDecimal("2237")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG20260305162917.jpg", new BigDecimal("2791"), new BigDecimal("1824")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG20260305165418.jpg", new BigDecimal("1067"), new BigDecimal("1459")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_20260305_154339.jpg", new BigDecimal("2246"), new BigDecimal("1687")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/IMG_20260305_160422.jpg", new BigDecimal("2871"), new BigDecimal("1477")),
        new Location("", "https://uman-beings.github.io/UMG-Pictures/hughim.jpg", new BigDecimal("1976"), new BigDecimal("1648"))
      ));
    };
  }
}
