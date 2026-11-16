#!/usr/bin/env python3
"""Find likely official trailers; review matches before distributing seed data."""
import json, re, subprocess, unicodedata
from concurrent.futures import ThreadPoolExecutor
from urllib.parse import urlencode
from pathlib import Path

files = [Path('src/main/resources/seed/wetv-movies.json'), Path('src/main/resources/seed/motphim-movies.json')]
data = [(p, json.loads(p.read_text())) for p in files]
def normalize(s):
    s = unicodedata.normalize('NFD', s.lower().replace('đ', 'd'))
    return re.sub(r'[^a-z0-9]+', ' ', ''.join(c for c in s if not unicodedata.combining(c))).strip()
def walk(x):
    if isinstance(x, dict):
        if 'videoRenderer' in x: yield x['videoRenderer']
        else:
            for v in x.values(): yield from walk(v)
    elif isinstance(x, list):
        for v in x: yield from walk(v)
def search(item):
    title = item['title']
    if 'motphim.boats' in item.get('source_url',''):
        try:
            from bs4 import BeautifulSoup
            page = subprocess.run(['curl','-fsSL','--max-time','12',item['source_url']],capture_output=True,check=True).stdout
            meta = BeautifulSoup(page, 'html.parser').select_one('meta[property="og:title"]')
            raw = meta.get('content','') if meta else ''
            match = re.search(r'\(([^()]+)\)\s*\(\d{4}\)', raw)
            if match and re.search('[a-zA-Z]', match.group(1)):
                title = match.group(1)
        except Exception: pass
    query = 'https://www.youtube.com/results?' + urlencode({'search_query': title + ' trailer'})
    try:
        html = subprocess.run(['curl','-fsSL','--max-time','20','-A','Mozilla/5.0',query],capture_output=True,check=True).stdout.decode('utf-8')
        match = re.search(r'var ytInitialData = (\{.*?\});</script>', html)
        if not match: return None
        for v in walk(json.loads(match.group(1))):
            t = v.get('title',{}).get('runs',[{}])[0].get('text','')
            channel = v.get('ownerText',{}).get('runs',[{}])[0].get('text','')
            nt, name = normalize(t), normalize(title)
            if len(name) < 5 or name not in nt or not re.search(r'\b(trailer|teaser)\b',nt): continue
            if re.search(r'\b(review|reaction|fanmade|fan made|tap|episode|recap)\b',nt): continue
            if not any(x in normalize(channel) for x in ('wetv','tencent video','netflix','iqiyi','youku','mango tv','mg drama','vieon','viki global tv')): continue
            vid = v.get('videoId','')
            if re.fullmatch(r'[\w-]{11}',vid): return (vid,t,channel)
    except Exception: return None
    return None
all_items = [x for _, rows in data for x in rows if not x.get('youtube_id')]
with ThreadPoolExecutor(max_workers=3) as pool:
    for item, result in zip(all_items, pool.map(search, all_items)):
        if result:
            item['youtube_id'] = result[0]
            print(item['title'], '|', result[1], '|', result[2], flush=True)
for path, rows in data:
    path.write_text(json.dumps(rows,ensure_ascii=False,indent=2)+'\n')
print('Found', sum(bool(x.get('youtube_id')) for _,rows in data for x in rows), 'of', sum(len(rows) for _,rows in data))
