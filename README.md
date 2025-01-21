# TODO
- Tunear prompts para sección de WELL_BEING
- Si regeneramos artículos porque las imágenes no casan bien, hay que eliminar las imágenes que no se usan
- Añadir negativePrompt para texto y/o imágenes
- Permitir que el método regenerate tenga prompts. Decidir cuales son los mejores
- Optimización de performance y Accessibility para superar el 90 en todas las métricas
- Interlinking de artículos
- Trazar estrategia de Backlinks
- Evaluar usar modelos Nova de Amazon (Canvas y Reels)
- Generación de otros tipos de contenido (guias de compra, productos afiliados, videos, simulador de decorados, etc)
- Integración con redes sociales
- Contenido para redes sociales
  - Posts
  - Stories
  - Reels
  - Videos
  - Galerias de imágenes



# Queries

## Contenido pendiente de publicar
```sql
select *
from deco.article_index ai 
where status = 'READY_TO_PUBLISH';

select *
from deco.image_index ii 
where status = 'READY_TO_PUBLISH';
```

## Imágenes huérfanas
```sql
SELECT i.*
FROM deco.image_index i
LEFT JOIN deco.article_image ai
ON i.id = ai.image_id
WHERE ai.image_id IS NULL;
```

