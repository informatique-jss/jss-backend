import { ICSEvent } from "./ICSEvent"

export const createEvent = (events: ICSEvent[], filename: string) => {
  const formatDate = (date: Date): string => {
    if (!date) {
      return ''
    }
    // don't use date.toISOString() here, it will be always one day off (cause of the timezone)
    const day = date.getDate() < 10 ? '0' + date.getDate() : date.getDate()
    const month = date.getMonth() < 9 ? '0' + (date.getMonth() + 1) : (date.getMonth() + 1)
    const year = date.getFullYear()
    const hour = date.getHours() < 10 ? '0' + date.getHours() : date.getHours()
    const minutes = date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()
    const seconds = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
    return `${year}${month}${day}T${hour}${minutes}${seconds}`
  }
  let VCALENDAR = `BEGIN:VCALENDAR
PRODID:-//Events Calendar//HSHSOFT 1.0//DE
VERSION:2.0
`

  for (const event of events) {
    const timeStamp = formatDate(new Date())
    const uuid = `${timeStamp}Z-uid@hshsoft.de`
    /**
     * Don't ever format this string template!!!
     */
    const EVENT = `BEGIN:VEVENT
DTSTAMP:${timeStamp}Z
DTSTART:${formatDate(event.start)}
DTEND:${formatDate(event.end)}
SUMMARY:${event.summary}
DESCRIPTION:${event.description}
X-ALT-DESC;FMTTYPE=text/html:${event.htmlDescription}
LOCATION:${event.location}
URL:${event.url}
UID:${uuid}
END:VEVENT`
    VCALENDAR += `${EVENT}
`
  }
  VCALENDAR += `END:VCALENDAR`

  return download(filename, VCALENDAR);
}

export const download = (filename: string, text: string) => {
  const element = document.createElement('a')
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text))
  element.setAttribute('download', filename)
  element.setAttribute('target', '_blank')
  element.style.display = 'none'
  element.click()
}
