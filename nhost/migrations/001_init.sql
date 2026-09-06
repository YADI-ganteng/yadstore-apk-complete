-- Users table
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email TEXT UNIQUE NOT NULL,
  username TEXT,
  balance INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT NOW()
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID REFERENCES users(id),
  game TEXT,
  item TEXT,
  price INTEGER,
  status TEXT DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT NOW()
);

-- Games table
CREATE TABLE IF NOT EXISTS games (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name TEXT UNIQUE,
  icon TEXT,
  items TEXT[],
  prices INTEGER[]
);

-- Insert sample games
INSERT INTO games (name, items, prices) VALUES
('Mobile Legends', ARRAY['3 DM', '5 DM', '12 DM', '19 DM', '28 DM'], ARRAY[1500, 2500, 6000, 9500, 14000]),
('Free Fire', ARRAY['5 DM', '12 DM', '50 DM', '70 DM', '140 DM'], ARRAY[1000, 2400, 10000, 14000, 28000]),
('PUBG Mobile', ARRAY['60 UC', '325 UC', '660 UC'], ARRAY[15000, 80000, 160000]),
('Genshin Impact', ARRAY['60 GC', '330 GC', '1090 GC'], ARRAY[15000, 80000, 260000]),
('Valorant', ARRAY['475 VP', '1000 VP', '2050 VP'], ARRAY[50000, 100000, 200000])
ON CONFLICT (name) DO NOTHING;
