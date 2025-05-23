export const createPopupContent = (name: string, centerId: number): HTMLElement => {
  const content = document.createElement('div');
  content.classList.add('popup-content');

  const textWrapper = document.createElement('div');
  textWrapper.classList.add('text-wrapper');
  textWrapper.innerText = name;
  content.appendChild(textWrapper);

  const button = document.createElement('button');
  button.innerText = 'View Details';
  button.classList.add('popup-button');
  button.onclick = () => {
    window.location.href = `/center/detailing/${centerId}`;
  };
  content.appendChild(button);

  return content;
};

export const parseLinkHeader = (header: string | null): Record<string, string> => {
  const links: Record<string, string> = {};
  if (!header) return links;
  const parts = header.split(',');
  parts.forEach(part => {
    const section = part.split(';');
    if (section.length !== 2) return;
    const url = section[0].trim().slice(1, -1);
    const name = section[1].trim().replace(/rel="(.*)"/, '$1');
    links[name] = url;
  });
  return links;
};
