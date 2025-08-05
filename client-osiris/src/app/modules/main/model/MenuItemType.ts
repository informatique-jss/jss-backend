export type MenuItemType = {
  label: string
  isTitle?: boolean
  icon?: string
  url?: string
  badge?: {
    variant: string
    text: string
  }
  target?: string
  isDisabled?: boolean
  isSpecial?: boolean
  children?: MenuItemType[]
  isCollapsed?: boolean
}
