#!/usr/bin/env python3
"""Collect public catalog metadata for seed data (no video streams)."""
import json, time, subprocess
from concurrent.futures import ThreadPoolExecutor
from urllib.parse import urljoin
from bs4 import BeautifulSoup

BASE = 'https://motphim.boats'
HEADERS = {'User-Agent': 'Mozilla/5.0 (compatible; MovieCatalog/1.0)'}
def fetch(path):
    result = subprocess.run(['curl', '-fsSL', '--max-time', '20', '-A', HEADERS['User-Agent'], urljoin(BASE, path)], capture_output=True, check=True)
    return BeautifulSoup(result.stdout, 'html.parser')

paths = set()
for listing in ['/', '/phim-bo', '/phim-le', '/tv-shows']:
    soup = fetch(listing)
    paths.update(a['href'].split('?')[0] for a in soup.select('a[href^="/phim/"]'))
    time.sleep(.3)

def details(path):
    try:
        soup = fetch(path)
        title = soup.select_one('meta[property="og:title"]')
        image = soup.select_one('meta[property="og:image"]')
        if not title or not image: return None
        name = title.get('content', '').split(' (')[0].strip()
        genres = []
        label = soup.find(string=lambda x: x and x.strip() == 'Thể loại:')
        if label:
            container = label.parent.parent
            spans = container.select('span')
            if len(spans) > 1: genres = [x.strip() for x in spans[-1].get_text(' ', strip=True).split(',') if x.strip()]
        desc = ''
        heading = soup.find(lambda tag: tag.name in ('h2', 'h3') and tag.get_text(strip=True) == 'Nội dung phim')
        if heading:
            parent = heading.parent
            heading.extract()
            desc = parent.get_text(' ', strip=True)
        return {'source_id':'motphim:'+path.rsplit('/',1)[-1], 'source_url':urljoin(BASE,path), 'title':name,
                'poster_path':image.get('content',''), 'description':desc, 'genre':[{'genre_name':g} for g in genres]}
    except Exception as e:
        print('Failed:', path, type(e).__name__, flush=True)
        return None

with ThreadPoolExecutor(max_workers=3) as pool:
    films = [item for item in pool.map(details, sorted(paths)) if item]
with open('src/main/resources/seed/motphim-movies.json','w',encoding='utf-8') as out:
    json.dump(films,out,ensure_ascii=False,indent=2); out.write('\n')
print(f'Collected {len(films)} of {len(paths)}')
