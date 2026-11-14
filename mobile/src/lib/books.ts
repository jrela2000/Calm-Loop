export type Book = { title: string; author: string; blurb: string };

export const BOOKS: Book[] = [
  { title: 'Feeling Good: The New Mood Therapy', author: 'David D. Burns', blurb: 'The classic CBT guide to catching and challenging distorted thoughts.' },
  { title: 'The Anxiety and Phobia Workbook', author: 'Edmund J. Bourne', blurb: 'Step-by-step exercises for panic, worry and phobias.' },
  { title: 'DARE: The New Way to End Anxiety', author: 'Barry McDonagh', blurb: 'A practical approach to meeting panic instead of fighting it.' },
  { title: 'Retrain Your Brain: CBT in 7 Weeks', author: 'Seth J. Gillihan', blurb: 'A short, structured CBT workbook for anxiety and low mood.' },
  { title: 'Rewire Your Anxious Brain', author: 'Catherine M. Pittman', blurb: 'How the brain’s fear circuits work and how to calm them.' },
];

const query = (b: Book) => encodeURIComponent(`${b.title} ${b.author}`);
export const bookshopUrl = (b: Book) => `https://bookshop.org/search?keywords=${query(b)}`;
export const amazonUrl = (b: Book) => `https://www.amazon.com/s?k=${query(b)}&i=stripbooks`;
