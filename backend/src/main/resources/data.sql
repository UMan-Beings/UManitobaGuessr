-- Seed dev locations when the table is empty.
-- This supports up to 20 rounds in GameService.
INSERT INTO "LOCATION" ("name", "imageUrl", "corX", "corY")
SELECT seed.name, seed.image_url, seed.cor_x, seed.cor_y
FROM (
  VALUES
    ('University of Manitoba Admin Building', 'https://picsum.photos/seed/umg-01/1600/900', -97.1382000000000, 49.8079000000000),
    ('Human Ecology Building', 'https://picsum.photos/seed/umg-02/1600/900', -97.1343000000000, 49.8087000000000),
    ('University Centre', 'https://picsum.photos/seed/umg-03/1600/900', -97.1372000000000, 49.8096000000000),
    ('Engineering and Information Technology Complex', 'https://picsum.photos/seed/umg-04/1600/900', -97.1325000000000, 49.8084000000000),
    ('St. Paul\'s College', 'https://picsum.photos/seed/umg-05/1600/900', -97.1386000000000, 49.8101000000000),
    ('Winnipeg - The Forks', 'https://picsum.photos/seed/umg-06/1600/900', -97.1315000000000, 49.8852000000000),
    ('Winnipeg - Exchange District', 'https://picsum.photos/seed/umg-07/1600/900', -97.1404000000000, 49.8969000000000),
    ('Winnipeg - Assiniboine Park', 'https://picsum.photos/seed/umg-08/1600/900', -97.2283000000000, 49.8700000000000),
    ('Toronto - CN Tower', 'https://picsum.photos/seed/umg-09/1600/900', -79.3871000000000, 43.6426000000000),
    ('Vancouver - Gastown', 'https://picsum.photos/seed/umg-10/1600/900', -123.1085000000000, 49.2841000000000),
    ('Calgary - Peace Bridge', 'https://picsum.photos/seed/umg-11/1600/900', -114.0765000000000, 51.0533000000000),
    ('Montreal - Old Port', 'https://picsum.photos/seed/umg-12/1600/900', -73.5512000000000, 45.5076000000000),
    ('Ottawa - Parliament Hill', 'https://picsum.photos/seed/umg-13/1600/900', -75.6999000000000, 45.4236000000000),
    ('Regina - Wascana Centre', 'https://picsum.photos/seed/umg-14/1600/900', -104.6170000000000, 50.4251000000000),
    ('Saskatoon - River Landing', 'https://picsum.photos/seed/umg-15/1600/900', -106.6629000000000, 52.1266000000000),
    ('Halifax - Waterfront', 'https://picsum.photos/seed/umg-16/1600/900', -63.5744000000000, 44.6488000000000),
    ('Victoria - Inner Harbour', 'https://picsum.photos/seed/umg-17/1600/900', -123.3693000000000, 48.4222000000000),
    ('Quebec City - Chateau Frontenac', 'https://picsum.photos/seed/umg-18/1600/900', -71.2075000000000, 46.8123000000000),
    ('Edmonton - Legislature Grounds', 'https://picsum.photos/seed/umg-19/1600/900', -113.4976000000000, 53.5343000000000),
    ('St. John\'s - Signal Hill', 'https://picsum.photos/seed/umg-20/1600/900', -52.7068000000000, 47.5687000000000)
) AS seed(name, image_url, cor_x, cor_y)
WHERE NOT EXISTS (SELECT 1 FROM "LOCATION");
